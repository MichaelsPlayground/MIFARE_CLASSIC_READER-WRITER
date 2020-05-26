package com.example.tfg_final;


import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WriteHexFragment extends Fragment {
    MifareClassic mifareTag;
    TextView hexTag;

    public void setMifareTag(MifareClassic mifareTag) {
        this.mifareTag = mifareTag;
    }

    public WriteHexFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_write_hex, container, false);
        hexTag = v.findViewById(R.id.hex_text);
        WriteMifareClassicTask task = new WriteMifareClassicTask(mifareTag);
        task.execute();
        return v;
    }


    private class WriteMifareClassicTask extends AsyncTask<Void, Void, Void> {
        MifareClassic taskTag;
        int sectorNum = 0;
        int blockNum = 0;
        int blockInSector = 0;
        boolean success = false;
        public ArrayList<String> foundKeys;

        WriteMifareClassicTask(MifareClassic mifareTag) {
            this.taskTag = mifareTag;
            this.sectorNum = mifareTag.getSectorCount();
            this.blockNum = mifareTag.getBlockCount();
            this.blockInSector = blockNum / sectorNum;
            this.foundKeys = new ArrayList<String>();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPreExecute() {
            hexTag.setText("Writing Tag, don't remove it!");
            try {
                OpenFileDialog();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... params) {

            try {
                taskTag.connect();
                int invalidSector = 3;
                for (int s = 0; s < sectorNum; s++) {
                    if (taskTag.authenticateSectorWithKeyA(s, passwords.getInstance().obtainedKeys.get(s))) {
                        //Log.d("AUTH", "Autenticacion completa del sector: " + Integer.toString(s));
                        for (int b = 0; b < blockInSector; b++) {
                            int blockIndex = (s * blockInSector) + b;

                            if (blockIndex != 0 && blockIndex != invalidSector)
                                //Log.d("AUTH", "Intentando escribir en el bloque " + Integer.toString(blockIndex));
                                taskTag.writeBlock(blockIndex, passwords.getInstance().modifiedData.get(blockIndex));
                        }
                        invalidSector += 4;
                    }
                }
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (taskTag != null) {
                    try {
                        taskTag.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //display block
            if (success) {
                hexTag.setText("Tarjeta escrita correctamente");
            } else {
                hexTag.setText("Fallo al escribir los bloques");
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void OpenFileDialog() throws IOException {

            //Read file in Internal Storage
            //String path = getContext().getExternalFilesDir(null) + "/actual.mfd";
            File file = new File(getContext().getExternalFilesDir(null), "actual.mfd");
            FileInputStream fis = null;
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //InputStreamReader isr = new InputStreamReader(fis);
            ArrayList<byte[]> content = new ArrayList<byte[]>();
            ArrayList<byte[]> content2 = new ArrayList<byte[]>();
            try {

                byte[] aux = new byte[16];
                for (int j = 0; j < 64; j++) {
                    for (int k = 0; k < 16; k++) {
                        int index = j * 16 + k;
                        aux[k] = fileContent[index];
                    }
                    passwords.getInstance().modifiedData.add(aux);
                    aux = new byte[16];
                }
                content = passwords.getInstance().data;
                content2 = passwords.getInstance().modifiedData;
                aux = new byte[16];
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

}
