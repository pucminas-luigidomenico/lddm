package pucminas.computacao.luigi.yourmenu.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import pucminas.computacao.luigi.yourmenu.R;

public class SubmenuData {

    public static List<Object[]> getSubmenuData(Context context) {
        List<Object[]> items = new ArrayList<>();

        Object[] itemMovie = new Object[2];
        itemMovie[0] = ContextCompat.getDrawable(context, R.drawable.ic_ondemand_video_24dp);
        itemMovie[1] = context.getResources().getString(R.string.movies);

        items.add(itemMovie);

        Object[] itemPdf = new Object[2];
        itemPdf[0] = ContextCompat.getDrawable(context, R.drawable.ic_pdf_24dp);
        itemPdf[1] = context.getResources().getString(R.string.pdf);

        items.add(itemPdf);

        Object[] itemLink = new Object[2];
        itemLink[0] = ContextCompat.getDrawable(context, R.drawable.ic_link_24dp);
        itemLink[1] = context.getResources().getString(R.string.links);

        items.add(itemLink);

        return items;
    }
}
