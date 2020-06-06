package com.example.charitycare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.charitycare.R;
import com.github.barteksc.pdfviewer.PDFView;

import static com.example.charitycare.Admin.AdminHomeActivity.pFile;

public class PaymentFragment extends Fragment {

    PDFView pdfView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.payment_layout, container, false);

        pdfView = root.findViewById(R.id.payment_pdf_viewer);

        previewPdf();
        return root;
    }

    private void previewPdf(){

       pdfView.fromFile(pFile)
                .pages(0,2,1,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
    }
}
