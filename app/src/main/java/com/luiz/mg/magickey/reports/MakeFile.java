package com.luiz.mg.magickey.reports;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import com.luiz.mg.magickey.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MakeFile {

    private final File dir;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public MakeFile(File dir) {
        this.dir = dir;

        if (!dir.exists())
            dir.mkdirs();
    }

    public int savePdf(Bitmap bitmap, String fileName, String ref) {

        int widthA4 = Utils.PAGE_WIDTH;
        int heightA4 = Utils.PAGE_HEIGHT;

        //Número de páginas
        int numOfPages = (bitmap.getHeight()/heightA4) + 1;

        Bitmap[] bitmaps = new Bitmap[numOfPages];

        int heightBitmap = bitmap.getHeight()/numOfPages;

        File file = new File(dir, fileName + ".pdf");

        PdfDocument filePdf = new PdfDocument();
        Paint title = new Paint();

        for (int i = 0; i < numOfPages; ++i) {

            //Paginação
            String pag = (i + 1) +"/"+ numOfPages;

            //Primeira Página
            if (i == 0) {

                //Composição da página
                PdfDocument.PageInfo infoPdf = new PdfDocument.PageInfo.Builder(
                        widthA4, heightA4, i)
                        .create();

                //Começar página
                PdfDocument.Page pagePdf = filePdf.startPage(infoPdf);
                Canvas canvas = pagePdf.getCanvas();

                //Title
                title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                title.setTextSize(15);

                canvas.drawText(Utils.TITLE_REPORT, Utils.MARGIN_START,
                        Utils.MARGIN_TOP, title);

                //Subtitle
                canvas.drawText("Referente a: "+ref, Utils.MARGIN_START,
                        Utils.MARGIN_TOP + 20, title);

                //Bitmap
                bitmaps[i] = Bitmap.createBitmap(
                        bitmap, 0, i * heightBitmap, bitmap.getWidth(), heightBitmap);

                canvas.drawBitmap(bitmaps[i], null, new Rect(
                        Utils.MARGIN_START, Utils.MARGIN_TOP + 40,
                        Utils.PAGE_WIDTH - 20, Utils.PAGE_HEIGHT - 60), null);

                //Número da Página
                canvas.drawText(pag,
                        Utils.PAGE_WIDTH - 60, Utils.PAGE_HEIGHT - 30, title);

                //Finalizar Página
                filePdf.finishPage(pagePdf);

            } else {

                bitmaps[i] = Bitmap.createBitmap(
                        bitmap, 0, i * heightBitmap, bitmap.getWidth(), heightBitmap);

                PdfDocument.PageInfo infoPdf = new PdfDocument.PageInfo.Builder(
                        widthA4, heightA4, i)
                        .create();

                PdfDocument.Page pagePdf = filePdf.startPage(infoPdf);

                Canvas canvas = pagePdf.getCanvas();

                canvas.drawBitmap(bitmaps[i], null, new Rect(
                        20, 40,
                        Utils.PAGE_WIDTH - 20, Utils.PAGE_HEIGHT - 60), null);

                //Número da Página
                canvas.drawText(pag, Utils.PAGE_WIDTH - 60, Utils.PAGE_HEIGHT - 30, title);

                //Finalizar Página
                filePdf.finishPage(pagePdf);
            }

        }



        try {

            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            filePdf.writeTo(outputStream);
            outputStream.close();
            filePdf.close();

        } catch (IOException e) {

            e.printStackTrace();
            return -1; //Erro

        }

        return 1; //Sucesso
    }


}
