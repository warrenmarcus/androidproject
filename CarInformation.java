package com.finalproject.app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarInformation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarInformation extends Fragment {

    ////////////////////////////////
    //added by Warren
    private PopupWindow popupwindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout constraintLayout;
    ///////////////////////////////////////

    //////////////////////////////////////
    // Database
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference carRef = mDatabase.getReference();
    DatabaseReference userCarRef = carRef.child("user-cars").child(user);
    DatabaseReference specificCarRef;
    DatabaseReference mileageRef;
    DatabaseReference tireRotationRef;
    DatabaseReference oilChangeRef;
    String currentMiles;
    String oilChangeData;
    String tireRotationData;
    DatabaseReference currentCar;

    /////
    /////////////////////////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarInformation() {


        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarInformation.
     */
    // TODO: Rename and change types and number of parameters
    public static CarInformation newInstance(String param1, String param2) {
        CarInformation fragment = new CarInformation();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_information, container, false);

        final Button btnBrakes = view.findViewById(R.id.btnBrakes);
        final Button btnFluids = view.findViewById(R.id.btnFluids);
        final Button btnTires = view.findViewById(R.id.btnTires);
        final Button btnBattery = view.findViewById(R.id.btnBattery);
        final TextView currentMileage = view.findViewById(R.id.currentMileage);




        userCarRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String uid = ds.getKey();
                    specificCarRef = userCarRef.child(uid);
                    mileageRef = specificCarRef.child("mileage");
                    oilChangeRef = specificCarRef.child("tireDiameter");
                    tireRotationRef = specificCarRef.child("tireRotation");

                    oilChangeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            oilChangeData = snapshot.getValue().toString();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    tireRotationRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tireRotationData = snapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    mileageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currentMiles = snapshot.getValue().toString();
                            currentMileage.setText(currentMiles + "miles");
                            currentCar = mileageRef;

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // declaring the buttons here.

        // this is the height for the popup window. just place holders for now
//        final int pop_width = 800;
//        final int pop_height = 1000;


        constraintLayout = view.findViewById(R.id.CarInformationLayout);

        // this is the height for the popup window. just place holders for now
        final int pop_width = 600;
        final int pop_height = 600;

// the beginning of the button methods. im sure there is a neater way to do all of this
        btnBrakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.pop_up, null);

                popupwindow = new PopupWindow(container, pop_width,pop_height, true);
                popupwindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);




                //
                //Setting information
                TextView infoTitle = (TextView)container.findViewById(R.id.infoTitle);

                String healthPercent = String.valueOf(brakeHealth(currentMiles));
                infoTitle.setText(healthPercent + "%");

                ProgressBar healthBar = container.findViewById(R.id.progress_bar);
                healthBar.setMax(100);
                healthBar.setProgress(brakeHealth(currentMiles));


                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupwindow.dismiss();
                        return true;
                    }
                });
                Toast.makeText(getContext(),"Brakes", Toast.LENGTH_SHORT).show();
            }
        });


        btnFluids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.pop_up, null);

                popupwindow = new PopupWindow(container, pop_width, pop_height, true);
                popupwindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

                //
                // setting information
                TextView infoTitle = (TextView)container.findViewById(R.id.infoTitle);

                String healthPercent = String.valueOf(oilHealth(currentMiles,oilChangeData));
                infoTitle.setText(healthPercent + "%");

                ProgressBar healthBar = container.findViewById(R.id.progress_bar);
                healthBar.setMax(100);
                healthBar.setProgress(oilHealth(currentMiles,oilChangeData));

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupwindow.dismiss();
                        return true;
                    }
                });
                Toast.makeText(getContext(),"Engine Oil", Toast.LENGTH_SHORT).show();
            }
        });


        btnTires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.pop_up, null);

                popupwindow = new PopupWindow(container, pop_width, pop_height, true);
                popupwindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);


                //
                // setting information
                TextView infoTitle = (TextView)container.findViewById(R.id.infoTitle);


                String healthPercent = String.valueOf(tireHealth(currentMiles,tireRotationData));
                infoTitle.setText(healthPercent + "%");

                ProgressBar healthBar = container.findViewById(R.id.progress_bar);
                healthBar.setMax(100);
                healthBar.setProgress(tireHealth(currentMiles,tireRotationData));

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupwindow.dismiss();
                        return true;
                    }
                });
                Toast.makeText(getContext(),"Tires", Toast.LENGTH_SHORT).show();
            }
        });


        btnBattery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.pop_up, null);

                popupwindow = new PopupWindow(container, pop_width, pop_height, true);
                popupwindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);


                //
                // setting information
                TextView infoTitle = (TextView)container.findViewById(R.id.infoTitle);

                String healthPercent = String.valueOf(batteryHealth(currentMiles));
                infoTitle.setText(healthPercent + "%");

                ProgressBar healthBar = container.findViewById(R.id.progress_bar);
                healthBar.setMax(100);
                healthBar.setProgress(batteryHealth(currentMiles));

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupwindow.dismiss();
                        return true;
                    }
                });

                Toast.makeText(getContext(),"Battery", Toast.LENGTH_SHORT).show();

            }
        });






        return view;
    }


    public int oilHealth (String currentMileage, String oilChangeData){
        try {
            int milesUntilChange = Integer.parseInt(oilChangeData);
            double currentMileageInt = Integer.parseInt(currentMileage);
            double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100));


            return (int) percent;
        }
        catch(Exception e){
            return 0;
        }
    }
    public int tireHealth(String currentMileage, String tireRotationData){
        try {
            int milesUntilChange = Integer.parseInt(tireRotationData);
            double currentMileageInt = Integer.parseInt(currentMileage);
            double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100));

            return (int) percent;
        }
        catch(Exception e){
            return 0;
        }

   }

    public int batteryHealth (String currentMileage){
        try{
            double milesUntilChange = 50000;
            double currentMileageInt = Integer.parseInt(currentMileage);
            double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100));

            return (int) percent;
        }
        catch(Exception e){
            return 0;
        }
    }


    public int brakeHealth (String currentMileage) {
        try {
            double milesUntilChange = 50000;
            double currentMileageInt = Integer.parseInt(currentMileage);
            double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100));

            return (int) percent;
        }
        catch (Exception e){
            return 0;
        }
    }

}