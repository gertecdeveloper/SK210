package com.example.escposprinter.textparser;

import com.example.escposprinter.EscPosPrinterCommands;
import com.example.escposprinter.exceptions.EscPosConnectionException;
import com.example.escposprinter.exceptions.EscPosEncodingException;

public interface IPrinterTextParserElement {
    int length() throws EscPosEncodingException;
    IPrinterTextParserElement print(EscPosPrinterCommands printerSocket) throws EscPosEncodingException, EscPosConnectionException;
}
