package com.example.charitycare.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.charitycare.R;
import com.example.charitycare.data.DisabledUsers;
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private DatabaseReference PostRef;
    private FirebaseRecyclerAdapter adapter;

    String phoneNumber, amount, disabiliType, fullName,help, currentUserId;

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

        try {
            createPdf(fetch());
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

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

    private List<DisabledUsers> fetch() {

        List<DisabledUsers> disabledUserList = new ArrayList<>();

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    fullName = snapshot.child("fullnames").getValue().toString();
                    amount = snapshot.child("amount").getValue().toString();
                    phoneNumber = snapshot.child("phonenumber").getValue().toString();
                    disabiliType = snapshot.child("Course").getValue().toString();
                    help = snapshot.child("Help").getValue().toString();

                    DisabledUsers disabledUsers = new DisabledUsers(
                            phoneNumber,
                            amount,
                            disabiliType,
                            fullName,
                            help
                    );
                    disabledUserList.add(disabledUsers);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(AdminHomeActivity.this, fullName, Toast.LENGTH_LONG).show();

        return disabledUserList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(AdminHomeActivity.this, String.valueOf(fetch().size()), Toast.LENGTH_LONG).show();
    }

    public void createPdf(List<DisabledUsers> disabledUsersList) throws DocumentException, FileNotFoundException {
        BaseColor lightGray = WebColors.getRGBColor("#606D80");
        BaseColor colorGreen = WebColors.getRGBColor("#1AA260");
        BaseColor grayColor = WebColors.getRGBColor("#425066");

        String Root = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Charity";
        File myFile = new File(Root);

        if(!myFile.exists())
            myFile.mkdir();
        File file = new File(myFile,"DisabledUsers.pdf");

        OutputStream output = new FileOutputStream(file);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{6, 10, 15, 25, 40, 10});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk suggText = new Chunk("Suggested Solution");
        PdfPCell suggCell = new PdfPCell(new Phrase(suggText));
        suggCell.setFixedHeight(50);
        suggCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        suggCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk noText = new Chunk("No.");
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk dateText = new Chunk("Date");
        PdfPCell dateCell = new PdfPCell(new Phrase(dateText));
        dateCell.setFixedHeight(50);
        dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dateCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk defText = new Chunk("Deficiency");
        PdfPCell defCell = new PdfPCell(new Phrase(defText));
        defCell.setFixedHeight(50);
        defCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        defCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk diagText = new Chunk("Diagnosed");
        PdfPCell diagCell = new PdfPCell(new Phrase(diagText));
        diagCell.setFixedHeight(50);
        diagCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        diagCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk footerText = new Chunk("Visione Softwares - Copyright @ 2020");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(6);


        table.addCell(noCell);
        table.addCell(dateCell);
        table.addCell(defCell);
        table.addCell(suggCell);
        table.addCell(diagCell);
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(lightGray);
        }
        for (int i = 0; i < disabledUsersList.size(); i++) {
            DisabledUsers def = disabledUsersList.get(i);

            String id = String.valueOf(i);
            String phone = def.getPhoneNumber();
            String amount = def.getAmount();
            String type = def.getDisabiliType();
            String fullname = def.getFullName();
            String help =def.getHelp();


            table.addCell(id + ". ");
            table.addCell(String.valueOf(phone));
            table.addCell(String.valueOf(amount));
            table.addCell(String.valueOf(type));
            table.addCell(String.valueOf(fullname));
            table.addCell(String.valueOf(help));

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 10, 15, 20, 25, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font f = new Font(Font.FontFamily.HELVETICA, 30.0f, Font.BOLD, colorGreen);
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph("Automated Maize Nitrogen Deficiency Detector (AMNDD) \n", f));
        document.add(new Paragraph("Monthly Report.\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();

    }
}
