package pri.z.show;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectPhotoDialog {

	public static void showSelectPhotoDialog(final Context mContext) {
		LinearLayout wayplanForm = (LinearLayout) ((Activity) mContext)
				.getLayoutInflater().inflate(R.layout.z_dialog_selectphoto, null);
		Button btnAlbum = (Button) wayplanForm
				.findViewById(R.id.z_dialogSelectAlbum);
		Button btnCamera = (Button) wayplanForm
				.findViewById(R.id.z_dialogSelectCamera);
		Button btnCansel = (Button) wayplanForm
				.findViewById(R.id.z_dialogSelectCansel);
		final MyDialog dialog = new MyDialog(mContext, R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();
		btnAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		btnCansel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

}
