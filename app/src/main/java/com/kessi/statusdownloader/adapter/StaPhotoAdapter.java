package com.kessi.statusdownloader.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.kessi.statusdownloader.PreviewStatusActivity;
import com.kessi.statusdownloader.R;
import com.kessi.statusdownloader.fragments.Utils;
import com.kessi.statusdownloader.model.StatusModel;
import com.kessi.statusdownloader.util.AdUtils;

import java.io.File;
import java.util.List;


public class StaPhotoAdapter extends BaseAdapter{

	Fragment context;
	List<StatusModel> arrayList;
	int width;
	public OnCheckboxListener onCheckboxListener;

	public StaPhotoAdapter(Fragment context, List<StatusModel> arrayList, OnCheckboxListener onCheckboxListener) {
		this.context = context;
		this.arrayList = arrayList;
		this.onCheckboxListener = onCheckboxListener;
		
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int screen_width = displayMetrics.widthPixels; // width of the device
		width = screen_width ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub

        View grid;
		LayoutInflater inflater = (LayoutInflater) context.getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		grid = new View(context.getActivity());
		if (arg1 == null) {
			grid = inflater.inflate(R.layout.ins_row_photo, null);
			
		} else {
			grid = (View) arg1;
		}

		grid = inflater.inflate(R.layout.ins_row_photo, null);


		ImageView share = grid.findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share( arrayList.get(arg0).getFilePath());
			}
		});

		grid.setLayoutParams(new GridView.LayoutParams((width*460/1080),
				(width*460/1080)));
		ImageView imageView = (ImageView) grid
				.findViewById(R.id.gridImage);

		Glide.with(context.getActivity()).load(arrayList.get(arg0).getFilePath()).into(imageView);

		CheckBox checkbox = grid.findViewById(R.id.checkbox);
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((StatusModel) arrayList.get(arg0)).setSelected(isChecked);
				if (onCheckboxListener != null) {
					onCheckboxListener.onCheckboxListener(buttonView, arrayList);
				}
			}
		});

		if (arrayList.get(arg0).isSelected()) {
			checkbox.setChecked(true);
		} else {
			checkbox.setChecked(false);
		}

		grid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Utils.mPath = arrayList.get(arg0).getFilePath();

				AdUtils.adCounter++;
				if (AdUtils.adCounter == AdUtils.adDisplayCounter) {
					if (!AdUtils.isLoadIronSourceAd) {
						AdUtils.showInterAd(context.getActivity(), null, 0);
					} else {
						AdUtils.ironShowInterstitial(context.getActivity(), null, 0);
					}
				}else {
					Intent intent = new Intent(context.getActivity(),
							PreviewStatusActivity.class);
					context.startActivityForResult(intent, 10);
				}
			}
		});
		return grid;
	}

	public interface OnCheckboxListener {
		void onCheckboxListener(View view, List<StatusModel> list);
	}

	void share(String path) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		share.setType("video/*");
		Uri photoURI = FileProvider.getUriForFile(
				context.getActivity().getApplicationContext(),
				context.getActivity().getApplicationContext()
						.getPackageName() + ".provider", new File(path));
		share.putExtra(Intent.EXTRA_STREAM,
				photoURI);
		context.startActivity(Intent.createChooser(share, "Share via"));
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("MyAdapter", "onActivityResult");
	}
}
