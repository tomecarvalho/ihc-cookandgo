package com.example.trueproject.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.trueproject.custom_classes.*;

import com.example.trueproject.R;

public class NotificationsFragment extends Fragment {
    private NotificationsViewModel notificationsViewModel;
    public static final String TAG = "";
    private ListView listView;
    private Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        this.listView = (ListView) root.findViewById(R.id.listView);
        this.button = (Button) root.findViewById(R.id.button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });
        this.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + position);
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                Log.i("isChecked", String.valueOf(currentCheck));
                Allergies al = (Allergies) listView.getItemAtPosition(position);
                al.setSelected(currentCheck);
            }
        });

        initListViewData();

        return root;
    }

    private void initListViewData() {
        Allergies[] als = Allergies.values();

        // android.R.layout.simple_list_item_checked:
        // ListItem is very simple (Only one CheckedTextView).
        ArrayAdapter<Allergies> arrayAdapter
                = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_checked, als);

        this.listView.setAdapter(arrayAdapter);

        for (int i = 0; i < als.length; i++)
            this.listView.setItemChecked(i, als[i].isSelected());
    }

    public void save(View view) {
        Log.i("amogus", "sus");
        for (int i = 0; i < listView.getCount(); i++) {
            Log.i("amogus", String.valueOf(i));
            Allergies al = (Allergies) listView.getItemAtPosition(i);
            Log.i("amogus", String.valueOf(al.isSelected()));
        }
    }
}

