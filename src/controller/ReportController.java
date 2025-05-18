package controller;

import dao.ReportDAO;
import exception.CustomException;
import model.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportController {
    private final ReportDAO dao = new ReportDAO();

    public List<Report> getFilteredReport(LocalDate startDate,
                                                    LocalDate endDate,
                                                    String status,
                                                    String bookTitle,
                                                    String username) throws CustomException {
        try{
            return dao.getFilteredReport(startDate, endDate, status, bookTitle, username);

        }catch(Exception e){
            throw new CustomException("Gagal mengambil data : " + e.getMessage());
        }
    }

    public List<String> getAllBookTitles() throws CustomException {
        try {
            return dao.getAllBookTitles();
        }catch(Exception e){
            throw new CustomException("Gagal mengambil data judul buku: " + e.getMessage());

        }
    }

    public List<String> getAllUsernames()throws CustomException {
        try {
            return dao.getAllUsernames();
        }catch(Exception e){
            throw new CustomException("Gagal mengambil data Username buku: " + e.getMessage());
        }
    }

    public Map<String, Integer> getMonthlyTrend(List<Report> data)  throws CustomException {
        try {
            return dao.calculateMonthlyTrend(data);
        }catch(Exception e){
            throw new CustomException("Gagal mengambil data trend buku: " + e.getMessage());
        }
    }

    public Map<String, Integer> getStatusDistribution(List<Report> data) throws CustomException {
        try {
            return data.stream()
                    .collect(Collectors.groupingBy(
                            Report::getStatus,
                            Collectors.summingInt(Report::getJumlahBuku)
                    ));
        }catch(Exception e){
            throw new CustomException("Gagal mengambil data distribusi buku: " + e.getMessage());
        }
    }

}