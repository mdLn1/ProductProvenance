package com.example.productprovenance;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NFCWriter {

    private NfcAdapter nfcAdapter;
    private Activity responsibleActivity;

    public NFCWriter(Activity responsibleActivity){
        this.responsibleActivity = responsibleActivity;
    }

    public int checkNFCWorking() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(responsibleActivity);

        if (nfcAdapter == null)
            return Constants.NFC_NOT_SUPPORTED;

        if (!nfcAdapter.isEnabled())
            return Constants.NFC_DISABLED;
        return Constants.NFC_ENABLED;
    }

    // starting https://github.com/survivingwithandroid/Surviving-with-android
    public void enableDispatch() {
        Intent nfcIntent = new Intent(responsibleActivity, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(responsibleActivity, 0, nfcIntent, 0);
        IntentFilter[] intentFiltersArray = new IntentFilter[] {};
        String[][] techList = new String[][] { { android.nfc.tech.Ndef.class.getName() }, { android.nfc.tech.NdefFormatable.class.getName() } };


        nfcAdapter.enableForegroundDispatch(responsibleActivity, pendingIntent, intentFiltersArray, techList);
    }

    public void disableDispatch() {
        nfcAdapter.disableForegroundDispatch(responsibleActivity);
    }



    public void writeTag(Tag tag, NdefMessage message)  {
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);

                if (ndefTag == null) {
                    // Let's try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                    }
                }
                else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public NdefMessage createTextMessage(String content) {
        try {
            // Get UTF-8 byte
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8"); // Content in UTF-8

            int langSize = lang.length;
            int textLength = text.length;

            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
            return new NdefMessage(new NdefRecord[]{record});
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // end
}
