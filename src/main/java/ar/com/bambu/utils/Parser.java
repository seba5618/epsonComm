package ar.com.bambu.utils;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.xml.sax.*;

public class Parser extends HandlerBase {
    private Vector data = null;
    private String logicalName;
    int x=-1;

    //Constructor de la Clase

    public Parser(String name, String LogicalName) {
        data = new Vector();
        logicalName=LogicalName;
        SAXParserFactory factory = SAXParserFactory.newInstance();

        //Apertura del xml
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(name), this);
        } catch (FileNotFoundException e) {
            System.out.println((new StringBuilder()).append("Error: ").append(e.
                    getMessage()).toString());
        } catch (SAXException e) {
            System.out.println((new StringBuilder()).append("Error: ").append(e.
                    getMessage()).toString());

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(0);
        }
    }

    //---------------------------------------------------------------------------//
//Retorna la cantidad de Datos
    public int getCount() {
        return data.size();
    }

//---------------------------------------------------------------------------//
//Retorna el datos cuyo indice es "i"

    public String getData(int i) {

        return (String) data.elementAt(i);
    }

    //---------------------------------------------------------------------------//


    public void startDocument() throws SAXException {
    }

//---------------------------------------------------------------------------//


    public void endDocument() throws SAXException {
    }

//---------------------------------------------------------------------------//

    public void startElement(String name, AttributeList attrs) throws
            SAXException {

        String Attrs;
        int a=-1;
        //System.out.println("OPEN XML");

        /*System.out.println("-----------------------------------");
                 System.out.println("DATA 0" +attrs.getValue(0));
                 System.out.println("DATA 1" +attrs.getValue(1));
                 System.out.println("DATA 2" +attrs.getValue(2));
                 System.out.println("DATA 3" +attrs.getValue(3));*/

        if (name.equals("JposEntry"))
        {

            Attrs=attrs.getValue(0);
            a = Attrs.compareTo(logicalName);
        } else {
            if (name.equals("peripheral"))
            {

                Attrs=attrs.getValue(0);
                a = Attrs.compareTo(logicalName);
            }

        }

        if(a==0)
        {
            data.add(attrs.getValue(0));
            //System.out.println(data.elementAt(0));
            x=0;
        }

        if (name.equals("prop"))
        {
            if(x==0)
            {
                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("OutputTimeout")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("OutputTimeout"+" "+data.elementAt(1));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("portName")) {
                    data.add(attrs.getValue(2));
                    //           System.out.println("portName"+" "+data.elementAt(2));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("OutputBufferSize")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("OutputBufferSize"+" "+data.elementAt(3));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("ReceiveTimeout")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("ReceiveTimeout"+" "+data.elementAt(4));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("stopBits")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("stopBits"+" "+data.elementAt(5));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("TimeoutTime")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("TimeoutTime"+" "+data.elementAt(6));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("dataBits")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("dataBits"+" "+data.elementAt(7));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("PortInterfaceName")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("PortInterfaceName"+" "+data.elementAt(8));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("Parity")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("Parity"+" "+data.elementAt(9));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("flowControl")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("flowControl"+" "+data.elementAt(10));
                }

                //----------------------------------------------------------------------
                if (attrs.getValue(0).equals("baudRate")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("baudRate"+" "+data.elementAt(11));
                }
                if (attrs.getValue(0).equals("firmwareVersion")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("firmwareVersion"+" "+data.elementAt(12));
                }
                if (attrs.getValue(0).equals("decimales")) {
                    data.add(attrs.getValue(2));
                    //System.out.println("firmwareVersion"+" "+data.elementAt(13));
                }
                if (attrs.getValue(0).equals("Enable")) {
                    data.add(attrs.getValue(2));
                    //  System.out.println("value peripheral"+" "+attrs.getValue(2) );
                }

            }
        }
        //----------------------------------------------------------------------

    }

//---------------------------------------------------------------------------//

    public void endElement(String s) throws SAXException {
    }

    //---------------------------------------------------------------------------//
    public void characters(char ac[], int i, int j) throws SAXException {
    }

    public void startElement2 (String name, AttributeList attrs)
            throws SAXException
    {


        if(name.equals("peripheral")){
            data.add(attrs.getValue(0));
        }
        if(name.equals("prop")){
            if((attrs.getValue(0)).equals("Enable")){
                data.add(attrs.getValue(2));
            }
            if((attrs.getValue(0)).equals("Category")){
                data.add(attrs.getValue(2));
            }
        }
    }

}
