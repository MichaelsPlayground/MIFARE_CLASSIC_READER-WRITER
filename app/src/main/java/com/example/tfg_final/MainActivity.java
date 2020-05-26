package com.example.tfg_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    Button readButton,readHexButton,editKeysButton,writeHexButton;
    ReadTagFragment readTagFragment = new ReadTagFragment();
    ReadHexTagFragment readHexTagFragment = new ReadHexTagFragment();
    WriteHexFragment writeHexFragment = new WriteHexFragment();
    MifareClassic mifareTag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,"Activa NFC para continuar",Toast.LENGTH_SHORT).show();
            finish();
        }
        readButton = findViewById(R.id.read_tag_button);
        readHexButton = findViewById(R.id.read_hex_tag);
        writeHexButton = findViewById(R.id.write_button);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                readTagFragment.setNfcAdapter(nfcAdapter);
                transaction.replace(R.id.main_fragment_layout, readTagFragment).addToBackStack("read");
                transaction.commit();
            }
        });
        readHexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                readHexTagFragment.setMifareTag(mifareTag);
                transaction.replace(R.id.main_fragment_layout, readHexTagFragment).addToBackStack("read_hex");
                transaction.commit();
            }
        });
        writeHexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                writeHexFragment.setMifareTag(mifareTag);
                transaction.replace(R.id.main_fragment_layout, writeHexFragment).addToBackStack("write_hex");
                transaction.commit();
            }
        });


    }
    public void setMifareTag(MifareClassic mifareTag){
        this.mifareTag = mifareTag;
    }
}
