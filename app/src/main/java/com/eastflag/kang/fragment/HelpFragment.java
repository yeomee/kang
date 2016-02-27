package com.eastflag.kang.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.eastflag.kang.R;

/**
 * 도움말 화면
 */
public class HelpFragment extends DialogFragment {


    public HelpFragment() {
        // Required empty public constructor
    }


    public static HelpFragment newInstance(int title) {
        HelpFragment frag = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        View view = View.inflate(getActivity(), R.layout.fragment_help, null);

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
/*                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((FragmentAlertDialog)getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((FragmentAlertDialog)getActivity()).doNegativeClick();
                            }
                        }
                )*/
                .create();

        return dialog;
    }

}
