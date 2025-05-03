package controller;

import dao.PopularBookDAO;
import exception.CustomException;
import model.PopularBookAnalysis;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.List;

public class PopularBookController {
    private final PopularBookDAO dao;

    public PopularBookController() {
        this.dao = new PopularBookDAO();
    }

    public List<PopularBookAnalysis> getPopularBooks(int tahun, int limit) throws CustomException {
        try {
            return dao.getPopularBooksAnalysis(tahun, limit);
        } catch (Exception e) {
            throw new CustomException("Gagal mengambil data buku: " + e.getMessage());

        }
    }

    public DefaultCategoryDataset createChartDataset(List<PopularBookAnalysis> books) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (PopularBookAnalysis book : books) {
            dataset.addValue(
                    book.getTotalPeminjaman(),
                    "Peminjaman",
                    book.getJudul()
            );
        }
        return dataset;
    }
}