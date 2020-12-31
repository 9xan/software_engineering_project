package com.example.provaapp.ModeCreate_2_1;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.provaapp.R;

public class RoomParentFragment extends Fragment { //HOST FRAGMENT

    public RoomParentFragment(){
        super(R.layout.room_parent_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //int someInt = requireArguments().getInt("2"); //get arguments from activity
        FragmentManager FragmentManagerMid = getParentFragmentManager();
        Bundle args = this.getArguments();
        FragmentManagerMid.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.room_parent_fragment_container, CreateRoomStep1.class, args)
                .commit();
    }



}
