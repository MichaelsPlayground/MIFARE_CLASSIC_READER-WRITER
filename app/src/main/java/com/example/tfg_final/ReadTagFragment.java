package com.example.tfg_final;


import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReadTagFragment extends Fragment {

    private NfcAdapter nfcAdapter;
    TextView idText, techText,mifareText;

    public void setNfcAdapter(NfcAdapter nfcAdapter) {
        this.nfcAdapter = nfcAdapter;
    }

    public ReadTagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_read_tag, container, false);
        idText = v.findViewById(R.id.tag_id);
        techText = v.findViewById(R.id.tag_tech);
        mifareText = v.findViewById(R.id.tag_mifare);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        //No puede convivir con mas apps de nfc
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Toast.makeText(getContext(),
                    "Se ha detectado una tarjeta NFC",
                    Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagInfo = "";
            String[] techList;
            if (tag == null) {
                idText.setText("TAG - Invalido");
            } else {
                for (int i = 0; i < tag.getId().length; i++) {
                    tagInfo += String.format("%02X", tag.getId()[i] & 0xff) + " ";
                }
                idText.setText("ID: " + tagInfo + "\nLENGTH: " + tag.getId().length);
                tagInfo = "";
                for (int i = 0; i < tag.getTechList().length; i++) {
                    tagInfo += tag.getTechList()[i] + "\n ";
                }
                techText.setText("TECH_LIST:\n " + tagInfo);

                if(tagInfo.contains("android.nfc.tech.MifareClassic")){
                    detectMifareTag(MifareClassic.get(tag));
                    ((MainActivity)getActivity()).setMifareTag(MifareClassic.get(tag));
                }else{
                    mifareText.setText("NO ES UN TAG MIFARE");
                }
            }
        }
    }

    public void detectMifareTag(MifareClassic mifareClassicTag){
        String info = "CARACTERISTICAS TAG MIFARE\n\n";
        switch(mifareClassicTag.getType()){
            case MifareClassic.TYPE_PLUS:
                info += "MifareClassic.TYPE_PLUS\n";
                break;
            case MifareClassic.TYPE_PRO:
                info += "MifareClassic.TYPE_PRO\n";
                break;
            case MifareClassic.TYPE_CLASSIC:
                info += "MifareClassic.TYPE_CLASSIC\n";
                break;
            case MifareClassic.TYPE_UNKNOWN:
                info += "MifareClassic.TYPE_UNKNOWN\n";
                break;
            default:
                info += "unknown...!\n";
        }
        switch(mifareClassicTag.getSize()){
            case MifareClassic.SIZE_1K:
                info += "MifareClassic.SIZE_1K\n";
                break;
            case MifareClassic.SIZE_2K:
                info += "MifareClassic.SIZE_2K\n";
                break;
            case MifareClassic.SIZE_4K:
                info += "MifareClassic.SIZE_4K\n";
                break;
            case MifareClassic.SIZE_MINI:
                info += "MifareClassic.SIZE_MINI\n";
                break;
            default:
                info += "unknown size...!\n";
        }
        info += "Numero de bloques \t= " + mifareClassicTag.getBlockCount() + "\n";

        info += "Numero de sectores \t= " + mifareClassicTag.getSectorCount() + "\n";

        mifareText.setText(info);
    }


}
