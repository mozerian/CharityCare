package com.example.charitycare.Admin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.charitycare.R;
import com.example.charitycare.data.DisabledUsers;
import com.example.charitycare.ui.DisableReportFragment;
import com.example.charitycare.ui.PaymentFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private DatabaseReference PostRef;
    private FirebaseRecyclerAdapter adapter;

    String phoneNumber, amount, disabiliType, fullName,help, currentUserId;
    //private DisabledUsers disabledUsers;
    List<DisabledUsers> disabledUserList;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int PERMISSION_ALL = 12;
    public static File mFile;

    private Button btnDisableReport, btnPaymentReport;
    private FrameLayout pdfViewer;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Disabled");

        disabledUserList = new ArrayList<>();

        file = new File("/storage/emulated/0/Charity/");
        if(!file.exists()) {
            file.mkdirs();
        }

        mFile = new File(file, "DisabledUsers.pdf");

        fetch();

        //pdfViewer = findViewById(R.id.frame_layout);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_disablelogout) {
            mAuth.signOut();
            SendUserToAdminLoginActivity();

        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToAdminLoginActivity() {
        Intent adminloginIntent = new Intent(AdminHomeActivity.this, AdminLOginActivity.class);
        adminloginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(adminloginIntent);
        finish();
    }

    private void fetch(){

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    DisabledUsers user = new DisabledUsers();
                    user.setFullnames(snapshot.child("fullnames").getValue().toString());
                    user.setAmount(snapshot.child("amount").getValue().toString());
                    user.setHelp(snapshot.child("Help").getValue().toString());
                    user.setPhoneNumber(snapshot.child("phonenumber").getValue().toString());
                    user.setCourse(snapshot.child("Course").getValue().toString());

                    disabledUserList.add(user);
                }

                try {
                    createPdf(mFile, disabledUserList);
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void checkPermissions(View view){

        if(hasPermissions(this, PERMISSIONS)){
            setDisableFragment();
            Toast.makeText(view.getContext(), "DisabledUsers.pdf saved to " + file.toString(), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public void createPdf(File mFile, List<DisabledUsers> disabledUsersList) throws DocumentException, FileNotFoundException {
        BaseColor lightGray = WebColors.getRGBColor("#606D80");
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");


        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(mFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{6, 25, 20, 20, 20, 20});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk noText = new Chunk("No.", white);
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk nameText = new Chunk("Name", white);
        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
        nameCell.setFixedHeight(50);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk phoneText = new Chunk("Phone Number", white);
        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
        phoneCell.setFixedHeight(50);
        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk disText = new Chunk("Disability Type", white);
        PdfPCell disCell = new PdfPCell(new Phrase(disText));
        disCell.setFixedHeight(50);
        disCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        disCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk helpText = new Chunk("Type of help", white);
        PdfPCell helpCell = new PdfPCell(new Phrase(helpText));
        helpCell.setFixedHeight(50);
        helpCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        helpCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk("Amount Required", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk footerText = new Chunk("Charity Care - Copyright @ 2020");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(6);


        table.addCell(noCell);
        table.addCell(nameCell);
        table.addCell(phoneCell);
        table.addCell(disCell);
        table.addCell(helpCell);
        table.addCell(amountCell);
        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();


       for (PdfPCell cell : cells) {
           cell.setBackgroundColor(grayColor);
        }
        for (int i = 0; i < disabledUsersList.size(); i++) {
            DisabledUsers def = disabledUsersList.get(i);

            String id = String.valueOf(i+1);
            String phone = def.getPhoneNumber();
            String amount = def.getAmount();
            String type = def.getCourse();
            String fullname = def.getFullnames();
            String help =def.getHelp();


            table.addCell(id + ". ");
            table.addCell(fullname);
            table.addCell(phone);
            table.addCell(type);
            table.addCell(help);
            table.addCell("KSH. " + amount);

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20, 20, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font f = new Font(Font.FontFamily.HELVETICA, 30.0f, Font.BOLD, colorBlue);
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph("Charity Care \n", f));
        document.add(new Paragraph("Registered Disabled Users Report.\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();

    }

    public boolean hasPermissions(Context context, String... permissions){
        if(context != null && permissions != null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return  true;
    }

    private void setDisableFragment(){
        DisableReportFragment disableReportFragment = new DisableReportFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, disableReportFragment);
        transaction.commit();
    }

    private void setPaymentFragment(){
        PaymentFragment disableReportFragment = new PaymentFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, disableReportFragment);
        transaction.commit();
    }

    public void previewPaymentReport(View view){
        setPaymentFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
