package com.kessi.statusdownloader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.ironsource.mediationsdk.IronSource;
import com.kessi.statusdownloader.fragments.Utils;
import com.kessi.statusdownloader.util.AdUtils;

import java.io.File;
import java.net.URLConnection;


public class PreviewStatusActivity extends AppCompatActivity {

	ImageView displayIV;
	VideoView displayVV;
	AlertDialog.Builder builder;

	LinearLayout btmLay;
	ImageView shareIV, deleteIV, wAppIV;

	LinearLayout adContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		btmLay = findViewById(R.id.btmLay);
		RelativeLayout.LayoutParams bParam = LayManager.relParams(PreviewStatusActivity.this,
				1080,360);
		bParam.addRule(RelativeLayout.ABOVE, R.id.banner_container);
		btmLay.setLayoutParams(bParam);

		shareIV = findViewById(R.id.shareIV);
		shareIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share();
			}
		});

		deleteIV = findViewById(R.id.deleteIV);
		deleteIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!AdUtils.isLoadIronSourceAd) {
					AdUtils.adCounter++;
					AdUtils.showInterAd(PreviewStatusActivity.this, null,0);
				} else {
					AdUtils.adCounter++;
					AdUtils.ironShowInterstitial(PreviewStatusActivity.this, null,0);
				}

				if (isVideoFile(Utils.mPath)){
					displayVV.pause();
				}

				delete();
			}
		});

		wAppIV = findViewById(R.id.wAppIV);
		wAppIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onWapp();
			}
		});

		LinearLayout.LayoutParams btnParam = LayManager.linParams(PreviewStatusActivity.this,
				184,220);
		shareIV.setLayoutParams(btnParam);
		deleteIV.setLayoutParams(btnParam);
		wAppIV.setLayoutParams(btnParam);

		displayIV = (ImageView) findViewById(R.id.displayIV);
		displayVV = (VideoView) findViewById(R.id.displayVV);

		if (isImageFile(Utils.mPath)) {
			Glide.with(PreviewStatusActivity.this)
					.load(Utils.mPath).into(displayIV);
			displayIV.setVisibility(View.VISIBLE);
			displayVV.setVisibility(View.INVISIBLE);
		} else if (isVideoFile(Utils.mPath)) {
			displayVV.setVideoPath(Utils.mPath);
			displayVV.start();
			displayIV.setVisibility(View.INVISIBLE);
			displayVV.setVisibility(View.VISIBLE);
		}
		
		builder = new AlertDialog.Builder(PreviewStatusActivity.this);
		builder.setTitle("Confirm Delete....");
		builder.setMessage("Are you sure, You Want To Delete This Status?");

		builder.setPositiveButton("Yes", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				File file = new File(Utils.mPath);
				file.delete();
				Toast.makeText(PreviewStatusActivity.this, "Status is deleted!!!",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();

				setResult(10, intent);

				// adapter.notifyDataSetChanged();
				finish();
				
			}
		});

		builder.setNegativeButton("No", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.cancel();
			}
		});


		adContainer = findViewById(R.id.banner_container);

		if (!AdUtils.isLoadIronSourceAd) {
			//admob
			AdUtils.initAd(PreviewStatusActivity.this);
			AdUtils.loadBannerAd(PreviewStatusActivity.this, adContainer);
			AdUtils.loadInterAd(PreviewStatusActivity.this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isVideoFile(Utils.mPath)) {
			displayVV.setVideoPath(Utils.mPath);
			displayVV.start();
			displayIV.setVisibility(View.INVISIBLE);
			displayVV.setVisibility(View.VISIBLE);
		}

		if (AdUtils.isLoadIronSourceAd) {
			AdUtils.destroyIron();
			AdUtils.ironBanner(PreviewStatusActivity.this, adContainer);
			// call the IronSource onResume method
			IronSource.onResume(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (AdUtils.isLoadIronSourceAd) {
			// call the IronSource onPause method
			IronSource.onPause(this);
		}
	}


    public static boolean isImageFile(String path) {
		String mimeType = URLConnection.guessContentTypeFromName(path);
		return mimeType != null && mimeType.startsWith("image");
	}

	public static boolean isVideoFile(String path) {
		String mimeType = URLConnection.guessContentTypeFromName(path);
		return mimeType != null && mimeType.startsWith("video");
	}

	public void share() {
		if (isImageFile(Utils.mPath)) {
			File imageFileToShare = new File(Utils.mPath);

			Intent share = new Intent(Intent.ACTION_SEND);
			share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			share.setType("image/*");
			Uri photoURI = FileProvider.getUriForFile(
					getApplicationContext(), getApplicationContext()
							.getPackageName() + ".provider", imageFileToShare);
			share.putExtra(Intent.EXTRA_STREAM,
					photoURI);
			startActivity(Intent.createChooser(share, "Share via"));

		} else if (isVideoFile(Utils.mPath)) {

			Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
					.getPackageName() + ".provider",new File(Utils.mPath));
			Intent videoshare = new Intent(Intent.ACTION_SEND);
			videoshare.setType("*/*");
			videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);

			startActivity(videoshare);
		}

	}

	public void onWapp() {
		if (isImageFile(Utils.mPath)) {
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/*");
			share.setPackage("com.whatsapp");
			File imageFileToShare = new File(Utils.mPath);
			Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
					.getPackageName() + ".provider",imageFileToShare);
			share.putExtra(Intent.EXTRA_STREAM, photoURI);
			startActivity(Intent.createChooser(share, "Share Image!"));
		} else if (isVideoFile(Utils.mPath)) {
			Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
					.getPackageName() + ".provider",new File(Utils.mPath));
			Intent videoshare = new Intent(Intent.ACTION_SEND);
			videoshare.setType("*/*");
			videoshare.setPackage("com.whatsapp");
			videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);

			startActivity(videoshare);
		}
	}
	
	public void delete(){
		builder.show();
	}


	@Override
	public void onBackPressed() {
		AdUtils.adCounter++;
		if (AdUtils.adCounter == AdUtils.adDisplayCounter) {
			if (!AdUtils.isLoadIronSourceAd) {
				AdUtils.showInterAd(PreviewStatusActivity.this, null, 0);
			} else {
				AdUtils.ironShowInterstitial(PreviewStatusActivity.this, null, 0);
			}
		} else {
			super.onBackPressed();
		}
	}
}
