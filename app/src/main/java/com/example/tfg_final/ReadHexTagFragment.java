package com.example.tfg_final;


import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReadHexTagFragment extends Fragment {
    MifareClassic mifareTag;
    TextView hexTag;

    public void setMifareTag(MifareClassic mifareTag) {
        this.mifareTag = mifareTag;
    }

    public ReadHexTagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_read_hex_tag, container, false);
        hexTag = v.findViewById(R.id.hex_text);
        ReadMifareClassicTask readMifare = new ReadMifareClassicTask(mifareTag);
        readMifare.execute();
        return v;
    }


    private class ReadMifareClassicTask extends AsyncTask<Void, Void, Void> {
        MifareClassic taskTag;
        int sectorNum = 0;
        int blockNum = 0;
        int blockInSector = 0;
        byte[][][] buffer;
        boolean success;
        public ArrayList<String> foundKeys;
        byte[] key = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ReadMifareClassicTask( MifareClassic mifareTag) {
            this.taskTag = mifareTag;
            this.sectorNum = mifareTag.getSectorCount();
            this.blockNum = mifareTag.getBlockCount();
            this.blockInSector = blockNum/sectorNum;
            this.buffer = new byte[sectorNum][blockInSector][MifareClassic.BLOCK_SIZE];
            this.foundKeys = new ArrayList<String>();
        }

        @Override
        protected void onPreExecute() {
            hexTag.setText("Reading Tag, don't remove it!");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                taskTag.connect();
                for (int s = 0; s < sectorNum; s++) {
                    key = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                    for (int i = 0; i < passwords.getInstance().customKeys.size(); i++) {
                        if (taskTag.authenticateSectorWithKeyA(s, passwords.getInstance().customKeys.get(i))) {
                            for (int b = 0; b < blockInSector; b++) {
                                int blockIndex = (s * blockInSector) + b;
                                buffer[s][b] = taskTag.readBlock(blockIndex);
                                passwords.getInstance().data.add(buffer[s][b]);
                            }
                            key = passwords.getInstance().customKeys.get(i);
                        }
                    }
                    String aux = "";
                    for(int i = 0; i < key.length ;i++)
                    {
                        aux += String.format("%02x", key[i]) + " ";
                    }
                    passwords.getInstance().obtainedKeys.add(key);

                    if(aux.equals("00 00 00 00 00 00 "))
                        foundKeys.add("Key not found");

                    else
                        foundKeys.add(aux);
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
                String stringBlock = "";
                for (int i = 0; i < sectorNum; i++) {
                    stringBlock += "CLAVE :"+ foundKeys.get(i) +"\nSECTOR "+ i + " :";
                    for (int j = 0; j < blockInSector; j++) {
                        for (int k = 0; k < MifareClassic.BLOCK_SIZE; k++) {
                            stringBlock += String.format("%02X", buffer[i][j][k] & 0xff) + " ";

                        }
                        stringBlock += "\n";
                    }
                    stringBlock += "\n";
                }
                writeFileExternalStorage(buffer);
                hexTag.setText(stringBlock);
            } else {
                hexTag.setText("Fallo al leer los bloques");
            }
        }

        public void writeFileExternalStorage(byte[][][] array) {

            //Text of the Document
            String textToWrite = "bla bla bla";

            //Checking the availability state of the External Storage.
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {

                //If it isn't mounted - we can't write into it.
                return;
            }

            //Create a new file that points to the root directory, with the given name:
            File file = new File(getContext().getExternalFilesDir(null), "actual.mfd");

            //This point and below is responsible for the write operation
            FileOutputStream outputStream = null;
            try {
                if(!file.exists())
                    file.createNewFile();
                //second argument of FileOutputStream constructor indicates whether
                //to append or create new file if one exists
                outputStream = new FileOutputStream(file, true);
                for (int i = 0; i < sectorNum; i++) {
                    for (int j = 0; j < blockInSector; j++) {
                        for (int k = 0; k < MifareClassic.BLOCK_SIZE; k++) {
                            outputStream.write(array[i][j][k]);
                        }
                    }
                }

                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
