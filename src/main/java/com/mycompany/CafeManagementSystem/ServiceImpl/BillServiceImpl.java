package com.mycompany.CafeManagementSystem.ServiceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.CafeManagementSystem.Entity.Bill;
import com.mycompany.CafeManagementSystem.JWT.JwtFilter;
import com.mycompany.CafeManagementSystem.Repository.BillRepository;
import com.mycompany.CafeManagementSystem.Service.IBillService;
import com.mycompany.CafeManagementSystem.Utils.CafeUtils;
import com.mycompany.CafeManagementSystem.constants.CafeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.file.Files.readAllBytes;

@Slf4j
@Service
public class BillServiceImpl implements IBillService {

    @Autowired
    BillRepository billRepository;

    @Autowired
    JwtFilter jwtFilter;


    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport method");
        try {
            String fileName;
            if (validateRequestMap(requestMap)) {
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }
                String data = "Name : " + requestMap.get("name") + "\n" + "Contact Number : " + requestMap.get("contactNumber") + "\n"
                         + "Email : " + requestMap.get("email") + "\n" + "Payment Method : " + requestMap.get("paymentMethod") + "\n";

                Document document = new Document();
                PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.PDF_LOCATION + "\\" + fileName + ".pdf"));

                document.open();
//                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph p = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(p);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n"
                + "Thank you for visiting our cafe. Visit again. ", getFont("Data"));
                document.add(footer);

                document.close();
                return new ResponseEntity<String>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows method");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader method");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        });
    }

    private Font getFont(String type) {
        log.info("Inside getFont method");
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.COURIER_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }

    }
//
//    private void setRectangleInPdf(Document document) throws DocumentException {
//        log.info("Inside setRectangleInPdf method");
//        Rectangle rectangle = new Rectangle(577, 825,18,15);
//        rectangle.enableBorderSide(1);
//        rectangle.enableBorderSide(2);
//        rectangle.enableBorderSide(4);
//        rectangle.enableBorderSide(8);
//        rectangle.setBorderColor(BaseColor.BLACK);
//        rectangle.setBorderWidth(1);
//        document.add(rectangle);
//    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Double.parseDouble((String) requestMap.get("totalAmount" )));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billRepository.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name")
                && requestMap.containsKey("email")
                && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("paymentMethod")
                && requestMap.containsKey("totalAmount")
                && requestMap.containsKey("productDetails");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if (jwtFilter.isAdmin()) {
            list = billRepository.getAllBills();
        } else {
            list = billRepository.getAllBillsByUser(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap{} ", requestMap);
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }
            String filePath = CafeConstants.PDF_LOCATION + "\\" + requestMap.get("uuid") + ".pdf";
            if (CafeUtils.isFileExist(filePath)) {
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }else {
                requestMap.put("isGenerate", false);
                generateReport(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    @Override
    public ResponseEntity<String> deleteBill(Long id) {
        try {
            Optional<Bill> bill = billRepository.findById(id);
            if (!bill.isEmpty()){
                billRepository.deleteById(id);
                return CafeUtils.getResponseEntity("Bill Deleted Successfully", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("Bill Id Does Not Exist", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
