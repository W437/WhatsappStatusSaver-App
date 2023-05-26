package com.kessi.statusdownloader.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.kessi.statusdownloader.DownActivity;
import com.kessi.statusdownloader.LayManager;
import com.kessi.statusdownloader.R;
import com.kessi.statusdownloader.model.StatusModel;
import com.kessi.statusdownloader.util.AdUtils;
import com.kessi.statusdownloader.util.Util;

import java.util.ArrayList;
import java.util.List;


public class RecWappAdapter extends BaseAdapter{

	Fragment context;
	List<StatusModel> arrayList;
	int width;
	public OnCheckboxListener onCheckboxListener;

	public RecWappAdapter(Fragment context, List<StatusModel> arrayList, OnCheckboxListener onCheckboxListener) {
		this.context = context;
		this.arrayList = arrayList;
		this.onCheckboxListener = onCheckboxListener;

		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int screen_width = displayMetrics.widthPixels; // width of the device
		width = screen_width ;
	}



	public List<StatusModel> getItemArray(){
		return  arrayList;
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
			grid = inflater.inflate(R.layout.ins_row_status, null);
			
		} else {
			grid = (View) arg1;
		}

		grid = inflater.inflate(R.layout.ins_row_status, null);


		ImageView play = grid.findViewById(R.id.play);

		RelativeLayout.LayoutParams params = LayManager.relParams(context.getActivity(), 90,90);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		play.setLayoutParams(params);

		if (!Util.getBack(arrayList.get(arg0).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()){
			play.setVisibility(View.VISIBLE);
		}else {
			play.setVisibility(View.GONE);
		}


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
				DownActivity.path = arrayList.get(arg0).getFilePath();

				Intent intent = new Intent(context.getActivity(), DownActivity.class);
				if (!AdUtils.isLoadIronSourceAd) {
					AdUtils.adCounter++;
					AdUtils.showInterAd(context.getActivity(), intent,0);
				} else {
					AdUtils.adCounter++;
					AdUtils.ironShowInterstitial(context.getActivity(), intent,0);
				}
			}
		});

		return grid;
	}

	public interface OnCheckboxListener {
		void onCheckboxListener(View view, List<StatusModel> list);
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("MyAdapter", "onActivityResult");
	}
}
