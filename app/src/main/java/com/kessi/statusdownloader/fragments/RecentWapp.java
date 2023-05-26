package com.kessi.statusdownloader.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.kessi.statusdownloader.R;
import com.kessi.statusdownloader.adapter.RecWappAdapter;
import com.kessi.statusdownloader.model.StatusModel;
import com.kessi.statusdownloader.util.AdUtils;
import com.kessi.statusdownloader.util.SharedPrefs;
import com.kessi.statusdownloader.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecentWapp extends Fragment implements RecWappAdapter.OnCheckboxListener {

    GridView imagegrid;
    ArrayList<StatusModel> f = new ArrayList<>();
    RecWappAdapter myAdapter;

    LinearLayout actionLay, downloadIV;
    CheckBox selectAll;
    ArrayList<StatusModel> filesToDelete = new ArrayList<>();

    RelativeLayout loaderLay, emptyLay;
    LinearLayout sAccessBtn;
    int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 101;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sta_photos, container, false);

        loaderLay = rootView.findViewById(R.id.loaderLay);
        emptyLay = rootView.findViewById(R.id.emptyLay);
        imagegrid = (GridView) rootView.findViewById(R.id.WorkImageGrid);

        actionLay = rootView.findViewById(R.id.actionLay);
        downloadIV = rootView.findViewById(R.id.downloadIV);
        downloadIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new downloadAll().execute();
            }
        });

        selectAll = rootView.findViewById(R.id.selectAll);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!AdUtils.isLoadIronSourceAd) {
                    AdUtils.adCounter++;
                    AdUtils.showInterAd(getActivity(), null, 0);
                } else {
                    AdUtils.adCounter++;
                    AdUtils.ironShowInterstitial(getActivity(), null, 0);
                }

                if (!compoundButton.isPressed()) {
                    return;
                }

                filesToDelete.clear();

                for (int i = 0; i < f.size(); i++) {
                    if (!f.get(i).selected) {
                        b = true;
                        break;
                    }
                }

                if (b) {
                    for (int i = 0; i < f.size(); i++) {
                        f.get(i).selected = true;
                        filesToDelete.add(f.get(i));
                    }
                    selectAll.setChecked(true);
                } else {
                    for (int i = 0; i < f.size(); i++) {
                        f.get(i).selected = false;
                    }
                    actionLay.setVisibility(View.GONE);
                }
                myAdapter.notifyDataSetChanged();
            }
        });

        sAccessBtn = rootView.findViewById(R.id.sAccessBtn);
        sAccessBtn.setOnClickListener(v -> {
            if (Util.appInstalledOrNot(getActivity(), "com.whatsapp")) {

                StorageManager sm = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);

                String statusDir = getWhatsupFolder();
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                    Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

                    String scheme = uri.toString();

                    scheme = scheme.replace("/root/", "/document/");

                    scheme += "%3A" + statusDir;

                    uri = Uri.parse(scheme);

                    intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse("content://com.android.externalstorage.documents/document/primary%3A" + statusDir));
                }


                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE);

            } else {
                Toast.makeText(getActivity(), "Please Install WhatsApp For Download Status!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        if (!SharedPrefs.getWATree(getActivity()).equals("")) {
            populateGrid();
        }

        return rootView;
    }

    class downloadAll extends AsyncTask<Void, Void, Void>{
        int success = -1;
        AlertDialog alertDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = Util.loadingPopup(getActivity());
            alertDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!filesToDelete.isEmpty()) {

                ArrayList<StatusModel> deletedFiles = new ArrayList<>();

                for (int i = 0; i < filesToDelete.size(); i++) {
                    StatusModel details = filesToDelete.get(i);
                    DocumentFile fromTreeUri = DocumentFile.fromSingleUri(getActivity(), Uri.parse(details.getFilePath()));
                    if (fromTreeUri.exists()) {
//                            Log.e("path: ", new File(details.getFilePath()).getAbsolutePath());
                        if (Util.download(getActivity(), details.getFilePath())) {
                            deletedFiles.add(details);
                            if (success == 0) {
                                break;
                            }
                            success = 1;
                        } else {
                            success = 0;
                        }
                    } else {
                        success = 0;
                    }

                }

                filesToDelete.clear();
                for (StatusModel deletedFile : deletedFiles) {
                    f.contains(deletedFile.selected = false);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            myAdapter.notifyDataSetChanged();
            if (success == 0) {
                Toast.makeText(getContext(), getResources().getString(R.string.save_error), Toast.LENGTH_SHORT).show();
            } else if (success == 1) {
                Toast.makeText(getActivity(), getResources().getString(R.string.save_success), Toast.LENGTH_SHORT).show();
            }
            actionLay.setVisibility(View.GONE);
            selectAll.setChecked(false);
            alertDialog.dismiss();

            if (!AdUtils.isLoadIronSourceAd) {
                AdUtils.adCounter++;
                AdUtils.showInterAd(getActivity(), null, 0);
            } else {
                AdUtils.adCounter++;
                AdUtils.ironShowInterstitial(getActivity(), null, 0);
            }
        }
    }

    public void populateGrid() {

        new loadDataAsync().execute();

    }

    class loadDataAsync extends AsyncTask<Void, Void, Void> {
        DocumentFile[] allFiles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaderLay.setVisibility(View.VISIBLE);
            imagegrid.setVisibility(View.GONE);
            sAccessBtn.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            allFiles = null;
            allFiles = getFromSdcard();
            for (int i = 0; i < allFiles.length - 1; i++) {
                if (!allFiles[i].getUri().toString().contains(".nomedia")) {
                    f.add(new StatusModel(allFiles[i].getUri().toString()));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            new Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    myAdapter = new RecWappAdapter(RecentWapp.this, f, RecentWapp.this);
                    imagegrid.setAdapter(myAdapter);
                    loaderLay.setVisibility(View.GONE);
                    imagegrid.setVisibility(View.VISIBLE);
                }

                if (f == null || f.size() == 0) {
                    emptyLay.setVisibility(View.VISIBLE);
                } else {
                    emptyLay.setVisibility(View.GONE);
                }
            }, 100);
        }
    }

    private DocumentFile[] getFromSdcard() {
        String treeUri = SharedPrefs.getWATree(getActivity());
        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(treeUri));
        if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory()
                && fromTreeUri.canRead() && fromTreeUri.canWrite()) {

            return fromTreeUri.listFiles();
        } else {
            return null;
        }
    }

    public String getWhatsupFolder() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + ".Statuses").isDirectory()) {
            return "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        } else {
            return "WhatsApp%2FMedia%2F.Statuses";
        }
    }


    @Override
    public void onCheckboxListener(View view, List<StatusModel> list) {
        filesToDelete.clear();
        for (StatusModel details : list) {
            if (details.isSelected()) {
                filesToDelete.add(details);
            }
        }
        if (filesToDelete.size() == f.size()) {
            selectAll.setChecked(true);
        }
        if (!filesToDelete.isEmpty()) {
            actionLay.setVisibility(View.VISIBLE);
            return;
        }

        selectAll.setChecked(false);
        actionLay.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
//            Log.e("onActivityResult: ", "" + data.getData());
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    requireContext().getContentResolver()
                            .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPrefs.setWATree(getActivity(), uri.toString());

            populateGrid();
        }

    }
}
