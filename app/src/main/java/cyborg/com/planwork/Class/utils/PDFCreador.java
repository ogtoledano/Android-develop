/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cyborg.com.planwork.Class.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.hyphenation.TernaryTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.MainActivity;

/**
 *
 * @author administrador
 */
public class PDFCreador {
    private String nombre;
    private List<Actividad> actividades;
    private String APP_FOLDER_NAME;
    private String fecha;
    private String autor;
    private String cargo;

    public PDFCreador() {
        this.APP_FOLDER_NAME="cyborg.com.planwork";
    }

    public void setDatos(String nombre, List<Actividad> actividades,String fecha){
        this.nombre=nombre;
        this.actividades=actividades;
        this.fecha=fecha;
    }

    public void setAutor(String autor){
        this.autor=autor;
    }
    
    public void crearDocumentoPDF() throws FileNotFoundException, DocumentException{
        Document documento=new Document();
        //Estilo carta;
        Rectangle rec=new Rectangle(792,612);
        documento.setPageSize(rec);
        String name=crearArchivo();
        PdfWriter.getInstance(documento, new FileOutputStream(new File(name)));
        documento.open();
        incluirMetadatos(documento);
        adicionarTitulo(documento,nombre);
        incluirAutorMes(documento);
        adicionarSaltoLinea(documento, 1);
        adicionarTabla(documento);
        documento.close();
    }

    private void incluirAutorMes(Document documento) throws DocumentException {
        Paragraph datos = new Paragraph();
        Paragraph dautor=new Paragraph("Nombre: "+autor);
        dautor.setAlignment(Element.ALIGN_RIGHT);
        datos.add(dautor);
        documento.add(datos);
    }

    private void incluirMetadatos(Document documento) {
        documento.addTitle(nombre);
        documento.addSubject("Creado el "+fecha);
        documento.addKeywords("planwork");
        documento.addAuthor(autor);
        documento.addCreator("com.cyborg.planwork");
    }

    private void adicionarTitulo(Document documento, String titulo) throws DocumentException {
        Paragraph prefacio = new Paragraph(titulo);
        prefacio.setAlignment(Element.ALIGN_CENTER);
        documento.add(prefacio);
    }
    
    private void adicionarTabla(Document documento) throws DocumentException{
        PdfPTable tabla=new PdfPTable(6);
        tabla.setWidthPercentage(100);
        SimpleDateFormat format=new SimpleDateFormat("d/M/yyyy");
        String [] dias={"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
        for(String dia:dias){
            Phrase frase=new Phrase(dia);
            frase.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD, BaseColor.WHITE));
            PdfPCell cell = new PdfPCell(frase);
            cell.setBorderColor(BaseColor.BLUE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLUE);
            tabla.addCell(cell);
        }
        Date date=new Date(fecha);
        int mes=date.getMonth();
        int espacios=date.getDay()-1;
        String [] diasActividades=new String[6];
        for(int i=0;i<espacios;i++){
            PdfPCell cell=new PdfPCell(new Phrase(" "));
            cell.setBorderColor(BaseColor.BLUE);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
            diasActividades[i]="";
        }
        int dia=1;
        int lastDia=1;
        int k=espacios;
        if(date.getDay()==0){
            date.setDate(++dia);
            k=0;
        }
        while(date.getMonth()==mes){
            if(date.getDay()!=0){ 
            PdfPCell cell = new PdfPCell(new Phrase(dia+""));
            cell.setBorderColor(BaseColor.BLUE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
            diasActividades[k++]=format.format(date);
            }else{
                for(int i=0;i<6;i++){
                    String aux=actividadesXDia(diasActividades[i]);
                    if(!aux.equals("")){
                        PdfPCell cell =new PdfPCell(new Phrase(aux));
                        cell.setBorderColor(BaseColor.BLUE);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla.addCell(cell);
                    }else{
                        PdfPCell cell = new PdfPCell(new Phrase(" "));
                        cell.setBorderColor(BaseColor.BLUE);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla.addCell(cell);
                    }
                }
                lastDia=dia;
                k=0;
            }
            date.setDate(++dia);
        }
        date.setMonth(date.getMonth()-1);
        date.setDate(lastDia+1);
        if(date.getMonth()==mes) {
            tabla.completeRow();
            for (int i = lastDia + 1; i < lastDia + 7; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(" "));
                date.setDate(i);
                if (date.getMonth() == mes) {
                    String valores = actividadesXDia(format.format(date));
                    if (!valores.equals("")) {
                        cell = new PdfPCell(new Phrase(valores));
                    }
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderColor(BaseColor.BLUE);
                tabla.addCell(cell);
            }
            PdfPRow penultima = tabla.getRow(tabla.getRows().size() - 2);
            for (PdfPCell cell : penultima.getCells()) {
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setBorderColor(BaseColor.BLUE);
            }
        }
        documento.add(tabla);

    }
    
    public void adicionarSaltoLinea(Document documento, int cant) throws DocumentException{
        for (int i = 0; i < cant; i++) {
            documento.add(new Paragraph(" "));
        }
    }

    private String crearArchivo(){
        String FILENAME = nombre+".pdf";
        //sdcard
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File pdfDir = new File(extStorageDirectory + File.separator + APP_FOLDER_NAME);

            if (!pdfDir.exists()) {
                pdfDir.mkdir();
            }

            String fullFileName = Environment.getExternalStorageDirectory() + File.separator + APP_FOLDER_NAME + File.separator + FILENAME;

            File outputFile = new File(fullFileName);

            if (outputFile.exists()) {
                outputFile.delete();
            }
        return fullFileName;
    }

    public String actividadesXDia(String fecha){
        String salida="";
        List<Actividad> aux=new LinkedList<>();
        for(Actividad act:actividades){
            SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
            if(format.format(act.getFecha()).equals(fecha)){
                aux.add(act);
            }
        }
        Collections.sort(aux);
        for(Actividad act:aux){
            salida+=act.getNombre()+"\n("+act.getHora()+")"+"\n";
        }
        return salida;
    }

    public void mostarPDF(Context context){

        String sdCardRoot = Environment.getExternalStorageDirectory().getPath();
        String path = sdCardRoot + File.separator + APP_FOLDER_NAME + File.separator + nombre+".pdf";

        File file = new File(path);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No hay aplicaciones disponibles para leer el documento", Toast.LENGTH_SHORT).show();
        }
    }

    private String mes(int mes){
        String [] meses= {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        return meses[mes];
    }
    
    
    
}
