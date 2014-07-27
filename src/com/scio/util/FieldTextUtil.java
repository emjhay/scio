package com.scio.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.scio.model.EmailValidator;
import com.scio.model.G;
import com.scio.ui.R;

public class FieldTextUtil {

	public static void checkFirstName(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_FIRSTNAME = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_FIRSTNAME = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkLastName(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_LASTNAME = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_LASTNAME = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkUsername(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_USERNAME = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_USERNAME = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkPassword(final EditText field,
			final EditText confirm, final ImageView icon,
			final ImageView icConfirm, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())
						&& field.getText().length() >= 6) {

					if (field.getText().toString()
							.equals(confirm.getText().toString())) {
						icon.setVisibility(View.VISIBLE);
						icon.setImageResource(R.drawable.ic_info_good);
						G.CHECK_PASSWORD = true;

						icConfirm.setImageResource(R.drawable.ic_info_good);
						G.CHECK_CONFIRM = true;

					} else {
						icon.setVisibility(View.VISIBLE);
						icon.setImageResource(R.drawable.ic_info_good);
						G.CHECK_PASSWORD = true;

						icConfirm.setImageResource(R.drawable.ic_info_bad);
						G.CHECK_CONFIRM = false;

					}
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_PASSWORD = false;

					icConfirm.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_CONFIRM = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkConfirmPassword(final EditText confirm,
			final EditText pass, final ImageView icon, final Button submit) {
		confirm.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(confirm.getText().toString())
						&& confirm.getText().toString()
								.equals(pass.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_CONFIRM = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_CONFIRM = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkEmail(final EditText field, final ImageView icon,
			final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())
						&& new EmailValidator().validate(field.getText()
								.toString()) == true) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_EMAIL = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_EMAIL = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void clientCheckEmail(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())
						&& new EmailValidator().validate(field.getText()
								.toString()) == true) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_CLIENT_EMAIL = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_CLIENT_EMAIL = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void clientCheckPass(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_CLIENT_PASS = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_CLIENT_PASS = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkPhoneNumber(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_PHONENUMBER = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_PHONENUMBER = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void checkaPhoneNumber(final EditText field,
			final ImageView icon, final Button submit) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_good);
					G.CHECK_ADMIN_PHONENUMBER = true;
				} else {
					icon.setVisibility(View.VISIBLE);
					icon.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_ADMIN_PHONENUMBER = false;
				}

				enableDisbleSubmitBtn(submit);

			}
		});
	}

	public static void popEmailTextWatcher(final EditText field,
			final ImageView icon, final ImageView icEmailImage) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())
						&& new EmailValidator().validate(field.getText()
								.toString()) == true) {
					icon.setImageResource(R.drawable.ic_info_good);
				} else {
					icon.setImageResource(R.drawable.ic_info_bad);
				}

				if (field.getText().toString().contains("gmail")) {
					// spin.setSelection(0);
					icEmailImage.setBackgroundResource(R.drawable.ic_gmail);
				} else if (field.getText().toString().contains("yahoo")) {
					// spin.setSelection(1);
					icEmailImage.setBackgroundResource(R.drawable.ic_yahoo);
				}

			}
		});
	}

	public static void popPassTextWatcher(final EditText field,
			final ImageView icon) {
		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {

				if (!StringUtil.isNullOrEmpty(field.getText().toString())) {
					icon.setImageResource(R.drawable.ic_info_good);
				} else {
					icon.setImageResource(R.drawable.ic_info_bad);
				}

			}
		});
	}

	private static void enableDisbleSubmitBtn(Button submit) {

		if (G.CHECK_FIRSTNAME && G.CHECK_LASTNAME && G.CHECK_USERNAME
				&& G.CHECK_EMAIL && G.CHECK_CLIENT_EMAIL && G.CHECK_CLIENT_PASS
				&& G.CHECK_PASSWORD && G.CHECK_CONFIRM && G.CHECK_PHONENUMBER
				&& G.CHECK_ADMIN_PHONENUMBER) {
			submit.setEnabled(true);
			submit.setClickable(true);
		} else {
			submit.setEnabled(false);
			submit.setClickable(false);
		}
	}

}
