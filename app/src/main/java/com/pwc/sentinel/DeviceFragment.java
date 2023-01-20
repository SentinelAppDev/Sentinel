package com.pwc.sentinel;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    private FirebaseAuth auth;
    private String email = "", selectedName = "", a = "";
    final List<String> names = new ArrayList<String>();
    private AppCompatSpinner dvcSpin;
    private ImageView blckMessenger, blckFacebook, blckInstagram, blckYouTube, blckTwitter, blckTikTok, blckDevice;

    public DeviceFragment() {}

    public static DeviceFragment newInstance(String param1, String param2) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null)
            email = users.getEmail();

        dvcSpin = (AppCompatSpinner) view.findViewById(R.id.fragmentdevicespinner);
        blckMessenger = (ImageView) view.findViewById(R.id.blockmessenger);
        blckFacebook = (ImageView) view.findViewById(R.id.blockfacebook);
        blckInstagram = (ImageView) view.findViewById(R.id.blockinstagram);
        blckYouTube = (ImageView) view.findViewById(R.id.blockyoutube);
        blckTwitter = (ImageView) view.findViewById(R.id.blocktwitter);
        blckTikTok = (ImageView) view.findViewById(R.id.blocktiktok);
        blckDevice = (ImageView) view.findViewById(R.id.blockdevice);
        String[] split = email.split("@");

        try{

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
            dr.child("Users").child(split[0]).child("Children").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot n : snapshot.getChildren()){
                        String a = n.getKey();
                        names.add(a);
                        ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                        namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dvcSpin.setAdapter(namesAdapter);
                        dvcSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView tv =(TextView) view;
                                selectedName = tv.getText().toString();
                                refresh();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                System.err.println("Nothing is selected on Spinner.");
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled Get children names: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("DeviceFragment onRetrieveNames: "+e);
        }

        blckMessenger.setOnClickListener(view1 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("ms").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveMsgrStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckMessenger onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckMessenger.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("ms").setValue("1");
                        break;
                    case "1":
                        blckMessenger.setImageResource(R.drawable.messenger);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("ms").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckMessenger: "+e);
            }
        });

        blckFacebook.setOnClickListener(view12 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("fb").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveFbStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckFacebook onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckFacebook.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("fb").setValue("1");
                        break;
                    case "1":
                        blckFacebook.setImageResource(R.drawable.facebook);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("fb").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckFacebook: "+e);
            }
        });

        blckInstagram.setOnClickListener(view13 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("ig").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveIgStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckInstagram onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckInstagram.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("ig").setValue("1");
                        break;
                    case "1":
                        blckInstagram.setImageResource(R.drawable.instagram);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("ig").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckInstagram: "+e);
            }
        });

        blckYouTube.setOnClickListener(view14 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("yt").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveYtStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckYouTube onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckYouTube.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("yt").setValue("1");
                        break;
                    case "1":
                        blckYouTube.setImageResource(R.drawable.youtube);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("yt").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckYoutube: "+e);
            }
        });

        blckTwitter.setOnClickListener(view15 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("tw").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveTwStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckTwitter onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckTwitter.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("tw").setValue("1");
                        break;
                    case "1":
                        blckTwitter.setImageResource(R.drawable.twitter);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("tw").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckTwitter: "+e);
            }
        });

        blckTikTok.setOnClickListener(view16 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("tk").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveTkStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckTikTok onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckTikTok.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("tk").setValue("1");
                        break;
                    case "1":
                        blckTikTok.setImageResource(R.drawable.tiktok);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("tk").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckTiktok: "+e);
            }
        });

        blckDevice.setOnClickListener(view17 -> {
            try{
                DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
                MsDr.child("Children").child(split[0]).child(selectedName).child("devicestatus").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            a = snapshot.getValue(String.class);
                        }
                        catch(Exception e){
                            System.err.println("onRetrieveDeviceStatus: "+e);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("DeviceFragment onCancelled blckDevice onClick: "+error);
                    }
                });
                switch(a){
                    case "0":
                        blckDevice.setImageResource(R.drawable.lock);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("devicestatus").setValue("1");
                        break;
                    case "1":
                        blckDevice.setImageResource(R.drawable.ic_baseline_lock_24);
                        MsDr.child("Children").child(split[0]).child(selectedName).child("devicestatus").setValue("0");
                        break;
                }
            }
            catch(Exception e){
                System.err.println("onClickblckDevice: "+e);
            }
        });
    }

    public void refresh(){
        String[] split = email.split("@");
        try{
            DatabaseReference MsDr = FirebaseDatabase.getInstance().getReference();
            MsDr.child("Children").child(split[0]).child(selectedName).child("ms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckMessenger.setImageResource(R.drawable.lock);
                                System.out.println("Changing layout to lock");
                                break;
                            case "0":
                                blckMessenger.setImageResource(R.drawable.messenger);
                                System.out.println("Changing layout to original");
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveMsgrStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckMessenger onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckMessenger: "+e);
        }
        refreshFb();
    }

    public void refreshFb(){
        String[] split = email.split("@");
        try{
            DatabaseReference FbDr = FirebaseDatabase.getInstance().getReference();
            FbDr.child("Children").child(split[0]).child(selectedName).child("fb").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckFacebook.setImageResource(R.drawable.lock);
                                break;
                            case "0":
                                blckFacebook.setImageResource(R.drawable.facebook);
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveFbStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckFacebook onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckFacebook: "+e);
        }
        refreshIg();
    }

    public void refreshIg(){
        String[] split = email.split("@");
        try{
            DatabaseReference IgDr = FirebaseDatabase.getInstance().getReference();
            IgDr.child("Children").child(split[0]).child(selectedName).child("ig").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckInstagram.setImageResource(R.drawable.lock);
                                break;
                            case "0":
                                blckInstagram.setImageResource(R.drawable.instagram);
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveIgStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckInstagram onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckInstagram: "+e);
        }
        refreshYt();
    }

    public void refreshYt(){
        String[] split = email.split("@");
        try{
            DatabaseReference YtDr = FirebaseDatabase.getInstance().getReference();
            YtDr.child("Children").child(split[0]).child(selectedName).child("yt").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckYouTube.setImageResource(R.drawable.lock);
                                break;
                            case "0":
                                blckYouTube.setImageResource(R.drawable.youtube);
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveYtStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckYouTube onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckYoutube: "+e);
        }
        refreshTw();
    }

    public void refreshTw(){
        String[] split = email.split("@");
        try{
            DatabaseReference tWdR = FirebaseDatabase.getInstance().getReference();
            tWdR.child("Children").child(split[0]).child(selectedName).child("tw").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckTwitter.setImageResource(R.drawable.lock);
                                break;
                            case "0":
                                blckTwitter.setImageResource(R.drawable.twitter);
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveTwStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckTwitter onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckTwitter: "+e);
        }
        refreshTk();
    }

    public void refreshTk(){
        String[] split = email.split("@");
        try{
            DatabaseReference TkDr = FirebaseDatabase.getInstance().getReference();
            TkDr.child("Children").child(split[0]).child(selectedName).child("tk").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckTikTok.setImageResource(R.drawable.lock);
                                break;
                            case "0":
                                blckTikTok.setImageResource(R.drawable.tiktok);
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveTkStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckTikTok onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckTiktok: "+e);
        }
        refreshDevice();
    }

    public void refreshDevice(){
        String[] split = email.split("@");
        try{
            DatabaseReference dev = FirebaseDatabase.getInstance().getReference();
            dev.child("Children").child(split[0]).child(selectedName).child("devicestatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        switch(Objects.requireNonNull(snapshot.getValue(String.class))){
                            case "1":
                                blckDevice.setImageResource(R.drawable.lock);
                                break;
                            case "0":
                                blckDevice.setImageResource(R.drawable.ic_baseline_lock_24);
                                break;
                        }
                    }
                    catch(Exception e){
                        System.err.println("onRetrieveDeviceStatus: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("DeviceFragment onCancelled blckDevice onClick: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onClickblckDevice: "+e);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
