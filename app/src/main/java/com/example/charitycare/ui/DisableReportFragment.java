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

import static com.example.charitycare.Admin.AdminHomeActivity.mFile;

public class DisableReportFragment extends Fragment {
    PDFView pdfView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.disable_report_layout, container, false);

        pdfView = root.findViewById(R.id.disable_pdf_viewer);

        previewPdf();
        return root;
    }

    private void previewPdf(){

        pdfView.fromFile(mFile)
                .pages(0,2,1,3,3,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
    }

    //this (mfile) shud be in DisableReportFragment
}
