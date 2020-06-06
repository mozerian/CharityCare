package com.example.charitycare.Admin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.charitycare.R;
import com.example.charitycare.data.DisabledUsers;
import com.example.charitycare.data.PaymentUsers;
import com.example.charitycare.ui.DisableReportFragment;
import com.example.charitycare.ui.PaymentFragment;
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
    private DatabaseReference payRef;

    String  currentUserId;

    List<PaymentUsers> paymentUsersList;

    //list of all permissions required
    List<DisabledUsers> disabledUserList;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int PERMISSION_ALL = 12;

    //disabled user disabledUserFile
    public static File mFile;

    //payment user disabledUserFile
    public static File pFile;

    private File disabledUserFile;
    private File payfile;


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
        payRef = FirebaseDatabase.getInstance().getReference().child("payment");


        disabledUserList = new ArrayList<>();
        paymentUsersList = new ArrayList<>();

        //create files in charity care folder
        disabledUserFile = new File("/storage/emulated/0/Charity/");
        payfile = new File("/storage/emulated/0/Charity/");

        //check if they exist, if not create them(directory)
        if (!disabledUserFile.exists() && !payfile.exists()) {
            disabledUserFile.mkdirs();
            payfile.mkdirs();
        }

        mFile = new File(disabledUserFile, "DisabledUsers.pdf");
        pFile = new File(payfile, "PaymentUsers.pdf");


        //fetch payment and disabled users details;
        fetchDisabledUsers();
        fetchPaymentUsers();

    }

    //database sasa
    //function to fetch payment data from the database
    private void fetchPaymentUsers() {
        payRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    PaymentUsers pays = new PaymentUsers();
                    pays.setUsernamepaid(snapshot.child("UsernamePaidFor").getValue().toString());
                    pays.setPhonenumberpaid(snapshot.child("PhoneNumberPaidFor").getValue().toString());
                    pays.setAmountpaid(snapshot.child("AmountPaid").getValue().toString());

        //this just log details fetched from db(you can use Timber for logging
                    Log.d("Payment", "Name: " + pays.getUsernamepaid());
                    Log.d("Payment", "Phone: " + pays.getPhonenumberpaid());
                    Log.d("Payment", "AMount: " + pays.getAmountpaid());

                    /* The error before was cause by giving incorrect data type
                    You were adding an object of type PaymentUsers yet the arraylist expects obejct of type DisabledUsers
                     */
                    paymentUsersList.add(pays);


                }
                //create a pdf file and catch exception beacause file may not be created
                try {
                    createPaymentReport(pFile, paymentUsersList);
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


    private void fetchDisabledUsers() {

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    DisabledUsers user = new DisabledUsers();
                    user.setFullnames(snapshot.child("fullnames").getValue().toString());
                    user.setAmount(snapshot.child("amount").getValue().toString());
                    user.setHelp(snapshot.child("Help").getValue().toString());
                    user.setPhoneNumber(snapshot.child("phonenumber").getValue().toString());
                    user.setCourse(snapshot.child("Course").getValue().toString());

                    disabledUserList.add(user);
                }

                try {
                    createDisabledUSersReport(mFile, disabledUserList);
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //responsible for original disabled users report generation

    public void createDisabledUSersReport(File mFile, List<DisabledUsers> disabledUsersList) throws DocumentException, FileNotFoundException {
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

            String id = String.valueOf(i + 1);
            String phone = def.getPhoneNumber();
            String amount = def.getAmount();
            String type = def.getCourse();
            String fullname = def.getFullnames();
            String help = def.getHelp();


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

    //responsible for generating payment report
    public void createPaymentReport(File mFile, List<PaymentUsers> paymentUsersList) throws DocumentException, FileNotFoundException {
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");


        //this is for another day
        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(mFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{6, 25, 20, 20});
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

        Chunk amountText = new Chunk("Amount Paid", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);


        Chunk footerText = new Chunk("Charity Care - Copyright @ 2020");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(4);


        table.addCell(noCell);
        table.addCell(nameCell);
        table.addCell(phoneCell);
        table.addCell(amountCell);
        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(grayColor);
        }
        for (int i = 0; i < paymentUsersList.size(); i++) {
            PaymentUsers pay = paymentUsersList.get(i);

            String id = String.valueOf(i + 1);
            String name = pay.getUsernamepaid();
            String amount = pay.getAmountpaid();
            String phone = pay.getPhonenumberpaid();


            table.addCell(id + ". ");
            table.addCell(name);
            table.addCell(phone);
            table.addCell(amount);

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font f = new Font(Font.FontFamily.HELVETICA, 30.0f, Font.BOLD, colorBlue);
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph("Charity Care \n", f));
        document.add(new Paragraph("Payment Report.\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();

    }

    //umeona vile nmechange vtu mob//yaps//ok

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setDisableFragment() {
        DisableReportFragment disableReportFragment = new DisableReportFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, disableReportFragment);
        transaction.commit();
    }

    private void setPaymentFragment() {
        PaymentFragment disableReportFragment = new PaymentFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, disableReportFragment);
        transaction.commit();
    }

    public void previewPaymentReport(View view) {
        if (hasPermissions(this, PERMISSIONS)) {
            setPaymentFragment();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    public void previewDisabledUsersReport(View view) {
        if (hasPermissions(this, PERMISSIONS)) {
            setDisableFragment();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }
    /* There was a function call checkPermissions(),
    that was called whenever Disabled Report button was clicked.
    It then sets the disabled fragment after checking that the permissions have been granted. So the problem is that it wa
    only cal setDiasbaleFragment and not PaymentFragment. So I had to create another function, previewDisabledUsersReport() for setting
    DisabledUsefFragment and Used previewPaymentReport() for setting PaymentFragment. Both Funtions check for if permissions
    are granted.
     I have set their click listeners in onClick in their layout(activity_admin_home.xml).

     */

    @Override
    protected void onStart() {

        super.onStart();
    }
}
//waah meona ako ka step kamwisho ndo kalkua nashindwa apo kwa pays