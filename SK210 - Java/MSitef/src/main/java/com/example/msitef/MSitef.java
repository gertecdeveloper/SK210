/*======================================================================================
  Arquivo       : MSitef.java
  Projeto       : Modo Quiosque
  Plataforma    : Android
  Equipamentos  : Dispositivos Android
  Data Criação  : 05/Nov/2020
  Autor         : Geovani Nogueira

  Descrição   : MSitef.java:
                   - Faz a chamada do MSitef via Intent e faz o tratamento dos retornos.
  =========================================================================================*/
package com.example.msitef;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MSitef {

    private Activity activity;

    public static int REQ_CODE_MSITEF = 4321;

    private Gson gson = new Gson();

    public MSitef(Activity act){
        this.activity = act;
    }

    public void ExecutaTEF(Bundle bundle) throws Exception {
        try{
            Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");
            intentSitef.putExtras(bundle);
            activity.startActivityForResult(intentSitef, REQ_CODE_MSITEF);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public RetornoMSitef traduzRetornoMSitef(Intent data) throws JSONException {
        RetornoMSitef retornoSitef = gson.fromJson(respSitefToJson(data), RetornoMSitef.class);
        return retornoSitef;
    }

    // O M-Sitef não retorna um json como resposta, logo é criado um json com a
    // reposta do Sitef.
    public String respSitefToJson(Intent data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("CODRESP", data.getStringExtra("CODRESP"));
        json.put("COMP_DADOS_CONF", data.getStringExtra("COMP_DADOS_CONF"));
        json.put("CODTRANS", data.getStringExtra("CODTRANS"));
        json.put("VLTROCO", data.getStringExtra("VLTROCO"));
        json.put("REDE_AUT", data.getStringExtra("REDE_AUT"));
        json.put("BANDEIRA", data.getStringExtra("BANDEIRA"));
        json.put("NSU_SITEF", data.getStringExtra("NSU_SITEF"));
        json.put("NSU_HOST", data.getStringExtra("NSU_HOST"));
        json.put("COD_AUTORIZACAO", data.getStringExtra("COD_AUTORIZACAO"));
        json.put("NUM_PARC", data.getStringExtra("NUM_PARC"));
        json.put("TIPO_PARC", data.getStringExtra("TIPO_PARC"));
        json.put("VIA_ESTABELECIMENTO", data.getStringExtra("VIA_ESTABELECIMENTO"));
        json.put("VIA_CLIENTE", data.getStringExtra("VIA_CLIENTE"));
        return json.toString();
    }

    public void dialodTransacaoAprovadaMsitef(RetornoMSitef retornoMsiTef) {
        AlertDialog alertDialog = new AlertDialog.Builder(this.activity).create();
        StringBuilder cupom = new StringBuilder();
        cupom.append("CODRESP: " + retornoMsiTef.getCodResp() + "\n");
        cupom.append("COMP_DADOS_CONF: " + retornoMsiTef.getCompDadosConf() + "\n");
        cupom.append("CODTRANS: " + retornoMsiTef.getCodTrans() + "\n");
        cupom.append("CODTRANS (Name): " + retornoMsiTef.getNameTransCod() + "\n");
        cupom.append("VLTROCO: " + retornoMsiTef.getvlTroco() + "\n");
        cupom.append("REDE_AUT: " + retornoMsiTef.getRedeAut() + "\n");
        cupom.append("BANDEIRA: " + retornoMsiTef.getBandeira() + "\n");
        cupom.append("NSU_SITEF: " + retornoMsiTef.getNSUSitef() + "\n");
        cupom.append("NSU_HOST: " + retornoMsiTef.getNSUHOST() + "\n");
        cupom.append("COD_AUTORIZACAO: " + retornoMsiTef.getCodAutorizacao() + "\n");
        cupom.append("NUM_PARC: " + retornoMsiTef.getParcelas() + "\n");
        alertDialog.setTitle("Ação executada com sucesso");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();
    }


    public void dialodTransacaoNegadaMsitef(RetornoMSitef retornoMsiTef) {
        AlertDialog alertDialog = new AlertDialog.Builder(this.activity).create();
        StringBuilder cupom = new StringBuilder();
        cupom.append("CODRESP: " + retornoMsiTef.getCodResp());
        alertDialog.setTitle("Ocorreu um erro durante a realização da ação");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();
    }

}
