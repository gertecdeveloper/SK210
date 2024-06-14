package com.example.easylayer_217_sk210;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.easylayer_217_sk210.Impressora.Impressora;
import com.example.easylayer_217_sk210.Projeto.Projeto;
import com.example.easylayer_217_sk210.Projeto.ProjetoAdapter;
import com.example.easylayer_217_sk210.Projeto.Sobre;
import com.example.easylayer_217_sk210.Scanner.Scanner;
import com.example.easylayer_217_sk210.tef.Tef;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Projeto> projetos = new ArrayList<Projeto>();
    ListView lvProjetos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Informações
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button informacoes = findViewById(R.id.infor);
        informacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

//Lista das funções
        lvProjetos = findViewById(R.id.lvProjetos);
        projetos.add(new Projeto("Impressão", R.drawable.print ));
        projetos.add(new Projeto("Scanner", R.drawable.barcode));
        projetos.add(new Projeto("Tef", R.drawable.pos));

        //Intent para as activitys das funções
        ProjetoAdapter adapter = new ProjetoAdapter(getBaseContext(), R.layout.listprojetos, projetos);
        lvProjetos.setAdapter(adapter);
        lvProjetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Projeto projeto = (Projeto) lvProjetos.getItemAtPosition(i);

                Intent intent = null;
                switch (projeto.getNome()){
                    case "Impressão":
                        intent = new Intent(MainActivity.this, Impressora.class);
                        break;
                    case "Scanner":
                        intent = new Intent(MainActivity.this, Scanner.class);
                        break;
                    case "Tef":
                        intent = new Intent(MainActivity.this, Tef.class);
                        break;
                }
                if(intent != null){
                    startActivity(intent);
                }
            }
        });
    }

    //Função do dialog de informações
    public void showDialog() {
        int mStackLevel = 1;
        mStackLevel++;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Sobre newFragment = Sobre.newInstance(mStackLevel);
        newFragment.show(ft, "dialog");
    }
}