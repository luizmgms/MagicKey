package com.luiz.mg.magickey.reports;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class MakeFile {

    private final File dir;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public MakeFile(File dir) {
        this.dir = dir;

        if (!dir.exists())
            dir.mkdirs();
    }

    public int createPdf(RecyclerView recyclerView, String fileName, String ref) {


        //Criar Documento pdf
        PdfDocument pdfDocument = new PdfDocument();

        //Número da página
        int num_of_page = 1;

        //Posição linha (Eixo Y)
        int pos_y = 40;

        //Informações da página
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                Utils.PAGE_WIDTH, Utils.PAGE_HEIGHT, num_of_page).create();

        //Começar a 1ª página
        PdfDocument.Page newPage = pdfDocument.startPage(pageInfo);

        //Canvas
        Canvas canvas = newPage.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        //Cabeçalho
        canvas.drawText("Relatório de Empréstimos de Chaves", Utils.MARGIN_START,
                pos_y, paint);
        pos_y = pos_y + 16;

        canvas.drawText("Referência: "+ref, Utils.MARGIN_START, pos_y,
                paint);
        pos_y = pos_y + 32;



        int tam = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();

        for (int i = 0; i < tam; i++) {

            RecyclerView.ViewHolder holder = recyclerView.getAdapter()
                    .createViewHolder(recyclerView,
                            recyclerView.getAdapter().getItemViewType(i));

            recyclerView.getAdapter().onBindViewHolder(holder, i);

            TextView nameKey = holder.itemView.findViewById(R.id.nameOfKeyId);
            TextView nameTake = holder.itemView.findViewById(R.id.nameOfUserTakedKeyId);
            TextView dateTimeTake = holder.itemView.findViewById(R.id.dateTimeTakedKeyId);
            TextView nameBack = holder.itemView.findViewById(R.id.nameOfUserBackedKeyId);
            TextView dateTimeBack = holder.itemView.findViewById(R.id.dateTimeBackedKeyId);

            //Se estiver no fim da página, cria uma nova
            if (pos_y >= Utils.PAGE_HEIGHT-74) {

                //Finaliza página anterior
                pdfDocument.finishPage(newPage);

                //Incrementa número da página
                num_of_page++;

                //Cria nova página
                pageInfo = new PdfDocument.PageInfo.Builder(
                        Utils.PAGE_WIDTH, Utils.PAGE_HEIGHT, num_of_page).create();
                newPage = pdfDocument.startPage(pageInfo);

                canvas = newPage.getCanvas();
                paint = new Paint();
                paint.setColor(Color.BLACK);

                //Reinicinando posição Y
                pos_y = 40;
            }

            //Nome da chave
            canvas.drawText(nameKey.getText().toString(), Utils.MARGIN_START, pos_y, paint);
            pos_y = pos_y + 16;

            //Nome de quem pegou, data e hora.
            canvas.drawText(nameTake.getText().toString() + " "
                    + dateTimeTake.getText().toString(), Utils.MARGIN_START, pos_y, paint);
            pos_y = pos_y + 16;

            //Nome de quem devolveu, data e hora.
            canvas.drawText(nameBack.getText().toString() + " "
                    + dateTimeBack.getText().toString(), Utils.MARGIN_START, pos_y, paint);
            pos_y = pos_y + 16;

            //Desenhar Linha
            canvas.drawLine(Utils.MARGIN_START, pos_y,
                    Utils.PAGE_WIDTH-Utils.MARGIN_START, pos_y, paint);
            pos_y = pos_y + 16;

            //Desenhar número da página
            canvas.drawText(num_of_page+"", Utils.PAGE_WIDTH - 40,
                    Utils.PAGE_HEIGHT - 20, paint);

        }

        //Finaliza página
        pdfDocument.finishPage(newPage);

        File file = new File(this.dir, fileName);

        try {

            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            pdfDocument.close();

            return 1;

        } catch (IOException e) {

            e.printStackTrace();
            return -1;

        }

    }

}
