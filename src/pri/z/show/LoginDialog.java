package pri.z.show;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class LoginDialog {

	public static void showLoginDialog(final Context mContext) {
		final LinearLayout wayplanForm = (LinearLayout) ((Activity) mContext)
				.getLayoutInflater().inflate(R.layout.z_dialog_login, null);
		Button btnSure = (Button) wayplanForm
				.findViewById(R.id.z_dialogLoginOK);
		Button btnCansel = (Button) wayplanForm
				.findViewById(R.id.z_dialogLoginNO);
		final MyDialog dialog = new MyDialog(mContext, R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, LoginOrRegister.class);
				((Activity) mContext).startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
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
