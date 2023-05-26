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

public class StaVideoAdapter extends BaseAdapter {

	private Fragment context;
	List<StatusModel> VideoValues;
	int width;
	public OnCheckboxListener onCheckboxListener;

	public StaVideoAdapter(Fragment context, List<StatusModel> videoValues, OnCheckboxListener onCheckboxListener) {
		super();
		this.context = context;
		VideoValues = videoValues;
		this.onCheckboxListener = onCheckboxListener;

		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int screen_width = displayMetrics.widthPixels; // width of the device
		width = screen_width ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return VideoValues.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	public interface OnCheckboxListener {
		void onCheckboxListener(View view, List<StatusModel> list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;
        gridView = new View(context.getActivity());

		if (convertView == null) {

			gridView = inflater.inflate(R.layout.ins_row_video, null);

		} else {
			gridView = (View) convertView;
		}

        gridView = inflater.inflate(R.layout.ins_row_video, null);

        ImageView imageThumbnail = (ImageView) gridView
                .findViewById(R.id.gridImageVideo);

		Glide.with(context.getActivity()).load(VideoValues.get(position).getFilePath()).into(imageThumbnail);


		ImageView share = gridView.findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share( VideoValues.get(position).getFilePath());
			}
		});


		CheckBox checkbox = gridView.findViewById(R.id.checkbox);
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((StatusModel) VideoValues.get(position)).setSelected(isChecked);
				if (onCheckboxListener != null) {
					onCheckboxListener.onCheckboxListener(buttonView, VideoValues);
				}
			}
		});

		if (VideoValues.get(position).isSelected()) {
			checkbox.setChecked(true);
		} else {
			checkbox.setChecked(false);
		}

		gridView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Utils.mPath = VideoValues.get(position).getFilePath();

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


		gridView.setLayoutParams(new GridView.LayoutParams((width *460/1080),
				(width *460/1080)));
		return gridView;
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
