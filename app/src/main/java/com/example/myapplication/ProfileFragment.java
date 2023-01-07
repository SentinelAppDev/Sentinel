package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth auth;
    private String email;
    private FloatingActionButton eab, sab;
    private OnFragmentInteractionListener mListener;
    private CircleImageView circleImageView;
    private TextView username, emailadd, contact, editpin, changePic;
    private EditText editname, editcontact;
    private Uri mImageUri;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param1, String param2) {

        ProfileFragment fragment = new ProfileFragment();
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

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null)
            email = users.getEmail();

        eab = (FloatingActionButton) view.findViewById(R.id.fragmentprofilebtnEdit);
        sab = (FloatingActionButton) view.findViewById(R.id.fragmentprofilebtnSave);
        circleImageView = (CircleImageView) view.findViewById(R.id.fragmentprofilepic);
        username = (TextView) view.findViewById(R.id.fragmentprofileusername);
        emailadd = (TextView) view.findViewById(R.id.fragmentprofileemailadd);
        contact = (TextView) view.findViewById(R.id.fragmentprofilecontact);
        editpin = (TextView) view.findViewById(R.id.fragmentprofileresetpin);
        editname = (EditText) view.findViewById(R.id.fragmentprofileeditname);
        editcontact = (EditText) view.findViewById(R.id.fragmentprofileeditcontact);
        changePic = (TextView) view.findViewById(R.id.fragmentprofilechangePic);

        emailadd.setText(String.format("Email Address: %s", email));

        editpin.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), newpin.class)));

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
