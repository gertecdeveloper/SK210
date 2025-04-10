package com.example.dv.nfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    // Constantes para mensagens
    private static final String ERROR_TAG_NOT_DETECTED = "Nenhuma tag NFC detectada!";
    private static final String ERROR_WRITE_FAILED = "Erro ao gravar na tag NFC, tente novamente.";
    private static final String ERROR_UNSUPPORTED_NDEF = "A tag NFC não suporta mensagens NDEF.";
    private static final String ERROR_DECODING_MESSAGE = "Erro ao decodificar mensagem NFC.";
    private static final String SUCCESS_WRITE = "Texto gravado com sucesso na tag NFC.";
    private static final String ERROR_NFC_NOT_SUPPORTED = "Dispositivo não suporta NFC.";

    private NfcAdapter nfcAdapter;
    private TextView textView;
    private PendingIntent pendingIntent;
    private IntentFilter[] writingTagFilters;
    private Tag myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC não suportado neste dispositivo", Toast.LENGTH_LONG).show();
            finish();
        }

        setupNfc();
    }

    private void setupNfc() {
        // Configura intenção para capturar eventos NFC
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[]{tagDetected};
    }


    private void readFromIntent(Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                displayTagContent(messages);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }


    private void displayTagContent(NdefMessage[] messages) {
        if (messages == null || messages.length == 0) {
            textView.setText("Nenhuma mensagem NDEF encontrada.");
            return;
        }

        try {
            NdefRecord[] records = messages[0].getRecords();
            if (records.length == 0) {
                textView.setText("Nenhum registro na mensagem NDEF.");
                return;
            }

            byte[] payload = records[0].getPayload();

            // Adicione verificações de segurança para o tamanho do payload
            if (payload == null || payload.length < 1) {
                textView.setText("Payload inválido ou vazio.");
                return;
            }

            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 63;

            // Verifica se o tamanho do languageCodeLength é válido
            if (languageCodeLength >= payload.length) {
                textView.setText("Tamanho do código de idioma é inválido.");
                return;
            }

            // Verifica se o restante do payload é suficiente para o texto
            int textStartIndex = 1 + languageCodeLength;
            if (textStartIndex >= payload.length) {
                textView.setText("Tamanho do payload não contém texto.");
                return;
            }

            // Decodifica o texto
            String text = new String(payload, textStartIndex, payload.length - textStartIndex, textEncoding);
            textView.setText("Conteúdo da Tag: " + text);

        } catch (UnsupportedEncodingException e) {
            Log.e("NFC_READ", "Erro ao decodificar mensagem NFC", e);
            textView.setText(ERROR_DECODING_MESSAGE);
        } catch (Exception e) {
            Log.e("NFC_READ", "Erro inesperado ao processar mensagem NFC", e);
            textView.setText("Erro inesperado ao ler a tag NFC.");
        }
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes(StandardCharsets.US_ASCII);
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }


}