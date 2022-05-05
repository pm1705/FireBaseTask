package com.example.firebasetask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fbrefs {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refWorkers = FBDB.getReference("Workers");
    public static DatabaseReference refCompanies = FBDB.getReference("Companies");
    public static DatabaseReference refMeals = FBDB.getReference("Meals");
}
