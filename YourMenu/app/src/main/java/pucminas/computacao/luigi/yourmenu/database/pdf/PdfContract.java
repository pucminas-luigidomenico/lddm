package pucminas.computacao.luigi.yourmenu.database.pdf;

import android.provider.BaseColumns;

public final class PdfContract {
    // Make constructor private to prevent someone accidentally instantiating
    // the contract class.
    private PdfContract() {}

    /* Inner class that defines the table contents */
    public static class PdfEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "pdf";

        // Table columns
        public static final String COLUMN_PDF_NAME = "pdfName";
        public static final String COLUMN_PDF_PATH = "pdfPath";
        public static final String COLUMN_MENU_PATH = "menuPath";
    }
}
