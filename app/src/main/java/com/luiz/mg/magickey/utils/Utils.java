package com.luiz.mg.magickey.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.LruCache;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class Utils {
     public static final String NAME_USER = "name";
     public static final String MAT_USER = "mat";
     public static final String DEPT_USER = "dept";
     public static final String NO_KEY = "NÃO";
     public static final String YES_KEY = "SIM";
     public static final String CREATE_FAIL = "Erro ao Criar Arquivo!";
     public static final String SAVE_SUCCESS = "Relatório Salvo com Sucesso!";
     public static final String SAVE_FAIL = "Erro ao tentar salvar Relátório!";

     //Dimensões da página PDF
     public static final int PAGE_HEIGHT = 1120;
     public static final int PAGE_WIDTH = 792;

     //Margens da página PDF
     public static final int MARGIN_START = 20;
     public static final int MARGIN_TOP = 40;

     //Posição do bitmap
     public static final String TITLE_REPORT = "Relatório de Entregas e Devoluções de Chaves";
     public static final String BACKED = "Devolvida por";
     public static final String DIRECTORY_REPORTS = "/ReportsKeys";
     public static final String ADDRESS_EMAIL_TO_SEND_REPORTS = "aplicacoes.capau@ifpi.edu.br";
     public static final String REPORT_EMPTY = "Relatório Vazio!";
     public static final String KEY = "Chave:";
     public static final String DELIVERED = "Entregue a";
     public static final String DAY = "Dia";
     public static final String NO_BACKED = "NÃO DEVOLVIDA!";
     public static final String sector = "-- Setor --";
     public static final String CAD = "Cadastrar";
     public static final String CANCEL = "Cancelar";
     public static final String ADD_USER_SUCCESS = "Usuário Adiconado com Sucesso!";
     public static final String ADD_USER_FAIL = "Erro ao cadastrar usuário!";
     public static final String ADD_USER_EXISTS = "Usuário já cadastrado!";
     public static final String ADD_KEY_FAIL = "Erro ao cadastrar chave!";
     public static final String ADD_KEY_SUCCESS = "Chave cadastrada com sucesso!";
     public static final String ADD_KEY_EXISTS = "Chave já cadastrada!";
     public static final String DEL = "Deletar";
     public static final String PICK_FILE_CSV = "Você deve escolher um arquivo .csv com " +
             "os campos separados por vírgula, como na imagem abaixo.";
     public static final String MONTH = "Mês";

     public static Bitmap screenShot (RecyclerView view) {
          //noinspection rawtypes
          RecyclerView.Adapter adapter = view.getAdapter();

          Bitmap bitmapReady = null;

          if (adapter != null){

               Paint paint = new Paint();

               int sizeList = adapter.getItemCount();
               int height = 0;
               int heightChange = 0;
               final int sizeMaxFile = (int)(Runtime.getRuntime().maxMemory() / 1024);
               final int sizeCache = sizeMaxFile / 8;

               LruCache<String, Bitmap> bitmapCache = new LruCache<>(sizeCache);

               for (int i = 0; i < sizeList; i++) {

                    RecyclerView.ViewHolder holder = adapter.createViewHolder(view,
                            adapter.getItemViewType(i));

                    adapter.onBindViewHolder(holder, i);

                    holder.itemView.measure(
                            View.MeasureSpec.makeMeasureSpec(view.getWidth(),View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                            holder.itemView.getMeasuredHeight());
                    //noinspection deprecation
                    holder.itemView.setDrawingCacheEnabled(true);
                    //noinspection deprecation
                    holder.itemView.buildDrawingCache();

                    //noinspection deprecation
                    Bitmap cacheBitmap = holder.itemView.getDrawingCache();
                    if (cacheBitmap != null) {
                         bitmapCache.put(String.valueOf(i),cacheBitmap);
                    }

                    height += holder.itemView.getMeasuredHeight();
               }

               bitmapReady = Bitmap.createBitmap(
                       view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);

               Canvas page = new Canvas(bitmapReady);
               page.drawColor(Color.WHITE);

               for (int i = 0; i < sizeList; i++) {

                    Bitmap bitmap = bitmapCache.get(String.valueOf(i));
                    page.drawBitmap(bitmap, 0, heightChange, paint);
                    heightChange += bitmap.getHeight();
                    bitmap.recycle();

               }

          }

          return bitmapReady;
     }
}
