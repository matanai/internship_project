package com.booking.web.service;

import com.booking.model.entity.User;
import com.booking.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class UserXLSXExportService
{
    private final UserRepository userRepository;

    private final XSSFWorkbook workbook = new XSSFWorkbook();
    private final XSSFSheet sheet = workbook.createSheet("users");

    @Autowired
    public UserXLSXExportService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void export(HttpServletResponse response) {
        try (OutputStream out = this.addHeaders(response).getOutputStream()) {
            setHeaderRow();
            setDataRows(userRepository.findAll());
            workbook.write(out);

            log.info("Users XLSX file successfully created");

        } catch (IOException e) {
            log.error("Failed to export to xlsx: {}", e.getMessage());
        }
    }

    private HttpServletResponse addHeaders(HttpServletResponse response) {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
        String fileName = "users_" + dateTime + ".xlsx";
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setContentType("application/octet-stream");
        response.setHeader(headerKey, headerValue);
        return response;
    }

    private void setHeaderRow() {
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        int columnCount = 0;
        createCell(row, columnCount++, "No", style);
        createCell(row, columnCount++, "Id", style);
        createCell(row, columnCount++, "First Name", style);
        createCell(row, columnCount++, "Last Name", style);
        createCell(row, columnCount++, "Username", style);
        createCell(row, columnCount++, "Password", style);
        createCell(row, columnCount++, "Roles", style);
        createCell(row, columnCount, "Enabled", style);
    }

    private void setDataRows(List<User> userList) {
        int rowCount = 1;

        for (User user : userList) {
            Row row = sheet.createRow(rowCount);
            int columnCount = 0;

            createCell(row, columnCount++, rowCount++, null);
            createCell(row, columnCount++, user.getId(), null);
            createCell(row, columnCount++, user.getFirstName(), null);
            createCell(row, columnCount++, user.getLastName(), null);
            createCell(row, columnCount++, user.getUsername(), null);
            createCell(row, columnCount++, user.getPassword(), null);
            createCell(row, columnCount++, user.getRoles(), null);
            createCell(row, columnCount, user.isEnabled(), null);
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(value.toString());

        if (style != null) {
            cell.setCellStyle(style);
        }
    }
}
