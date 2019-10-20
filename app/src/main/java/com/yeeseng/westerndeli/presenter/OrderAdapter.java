package com.yeeseng.westerndeli.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.style.light.Position;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Order_Item;
import com.yeeseng.westerndeli.view.FragCurOrder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class OrderAdapter extends ArrayAdapter<Order_Item> {

    private FragCurOrder activity;
    private List<Order_Item> order;

    public OrderAdapter(FragCurOrder context, int resource, List<Order_Item> objects) {
        super(context.getActivity(), resource, objects);
        this.activity = context;
        this.order = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getActivity()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.order_list_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position).getPortion() + "x "+ getItem(position).getItemName() + " RM" + getItem(position).getItemCost());

        //handling buttons event
        holder.btnEdit.setOnClickListener(onEditListener(position, holder));
        holder.btnDelete.setOnClickListener(onDeleteListener(position, holder));

        return convertView;
    }

    private View.OnClickListener onEditListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(position, holder);
            }
        };
    }

    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position, holder);
            }
        };
    }

    /**
     * Editting confirm dialog
     *
     * @param position
     * @param holder
     */
    private void showEditDialog(final int position, final ViewHolder holder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.getActivity());

        alertDialogBuilder.setTitle(R.string.changeporc);
        final EditText input = new EditText(activity.getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setText(order.get(position).getPortion().toString());
        input.setLayoutParams(lp);
        alertDialogBuilder.setView(input);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result edit text
                                if (checkPosWholeInteger(input.getText().toString())) {
                                    SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
                                    String username = prefs.getString("username", "No name defined");

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    db.collection("Users").document(username).collection("CurrentOrder")
                                            .document(getItem(position).getItemName()).update("Portion", Integer.valueOf(input.getText().toString()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    order.get(position).setPortion(Integer.valueOf(input.getText().toString()));

                                                    //notify data set changed
                                                    activity.updateAdapter();
                                                    holder.swipeLayout.close();

                                                    onButtonShowErrorWindowClick("Update Success!");
                                                }

                                            });
                                } else {
                                    onButtonShowErrorWindowClick("Input must be a positive whole number!");
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog and show it
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDeleteDialog(final int position, final ViewHolder holder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.getActivity());

        alertDialogBuilder.setMessage(R.string.confirmdel)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
                        String username = prefs.getString("username", "No name defined");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Users").document(username).collection("CurrentOrder")
                                .document(getItem(position).getItemName()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        order.remove(position);
                                        holder.swipeLayout.close();
                                        activity.updateAdapter();

                                        onButtonShowErrorWindowClick("Delete Success!");
                                    }

                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class ViewHolder {
        @BindView(R.id.content)
        TextView name;
        @BindView(R.id.delete)
        View btnDelete;
        @BindView(R.id.edit_query)
        View btnEdit;
        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        }

    }

    public void onButtonShowErrorWindowClick(String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean checkPosWholeInteger(String input) {
        boolean digitsOnly = TextUtils.isDigitsOnly(input);
        if (digitsOnly) {
            if (Integer.valueOf(input) % 1 == 0) {
                return true;
            }
        }
        return false;
    }


}
