package com.kessi.statusdownloader.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.kessi.statusdownloader.R;
import com.kessi.statusdownloader.adapter.StaPhotoAdapter;
import com.kessi.statusdownloader.model.StatusModel;
import com.kessi.statusdownloader.util.AdUtils;
import com.kessi.statusdownloader.util.Util;


import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaPhotos extends Fragment implements StaPhotoAdapter.OnCheckboxListener {

    GridView imagegrid;
    public static ArrayList<StatusModel> f = new ArrayList<>();
    public static StaPhotoAdapter myAdapter;
    int save = 10;
    LinearLayout actionLay, deleteIV;
    CheckBox selectAll;
    ArrayList<StatusModel> filesToDelete = new ArrayList<>();
    RelativeLayout loaderLay, emptyLay;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sta_myphotos, container, false);
        loaderLay = rootView.findViewById(R.id.loaderLay);
        emptyLay = rootView.findViewById(R.id.emptyLay);
        imagegrid = (GridView) rootView.findViewById(R.id.WorkImageGrid);
        populateGrid();

        actionLay = rootView.findViewById(R.id.actionLay);
        deleteIV = rootView.findViewById(R.id.deleteIV);
        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!filesToDelete.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure , You want to delete selected files?")
                            .setCancelable(true)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   new deleteAll().execute();
                                }
                            })
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
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
        return rootView;
    }

    class deleteAll extends AsyncTask<Void, Void, Void> {
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

            ArrayList<StatusModel> deletedFiles = new ArrayList<>();
            for (int i = 0; i < filesToDelete.size(); i++) {
                StatusModel details = filesToDelete.get(i);
                File file = new File(details.getFilePath());
                if (file.exists()) {
                    if (file.delete()) {
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
                f.remove(deletedFile);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            myAdapter.notifyDataSetChanged();
            if (success == 0) {
                Toast.makeText(getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
            } else if (success == 1) {
                Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagegrid.setVisibility(View.GONE);
            loaderLay.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getFromSdcard();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            new Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    myAdapter = new StaPhotoAdapter(StaPhotos.this, f, StaPhotos.this);
                    imagegrid.setAdapter(myAdapter);
                    loaderLay.setVisibility(View.GONE);
                    imagegrid.setVisibility(View.VISIBLE);

                    if (f == null || f.size() == 0) {
                        emptyLay.setVisibility(View.VISIBLE);
                    } else {
                        emptyLay.setVisibility(View.GONE);
                    }
                }
            }, 1000);
        }
    }

    public void getFromSdcard() {

        File file = new File(
                Environment
                        .getExternalStorageDirectory().toString() + "/Download/" + getResources().getString(R.string.app_name) + "/Images");
        f = new ArrayList<>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < listFile.length; i++) {

                f.add(new StatusModel(listFile[i].getAbsolutePath()));

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        myAdapter.onActivityResult(requestCode, resultCode, data);
        if (requestCode == save && resultCode == save) {
            myAdapter.notifyDataSetChanged();

            populateGrid();
            actionLay.setVisibility(View.GONE);
            selectAll.setChecked(false);
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
}
