package ru.kovbasa.pcspdf.search;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import ru.kovbasa.pcspdf.model.PageEntry;
import ru.kovbasa.pcspdf.services.StopList;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, Set<PageEntry>> index;

    public BooleanSearchEngine(File pdfsDir) {
        index = new HashMap<>();

        process(pdfsDir);
    }

    private void process(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    process(file);
                }
            }
        } else if (dir.getName().endsWith(".pdf")) {
            processFile(dir);
        }
    }

    private void processFile(File pdf) {
        PdfDocument doc = null;
        try {
            doc = new PdfDocument(new PdfReader(pdf));
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }

        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            int pageId = i + 1;

            PdfPage page = doc.getPage(pageId);

            String text = PdfTextExtractor.getTextFromPage(page);

            text = text.replaceAll("[" + (char) 769 + "]+", "");

            String[] words = text.split("\\P{IsAlphabetic}+");
            List<String> list = Arrays.stream(words)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            list.removeAll(StopList.getStopList());

            Map<String, Long> occurrences = list.stream()
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            for (Map.Entry<String, Long> entry : occurrences.entrySet()) {
                String key = entry.getKey();

                if (!index.containsKey(key)) {
                    index.put(key, new TreeSet<>());
                }

                PageEntry pageEntry = new PageEntry(pdf.getName(), pageId, entry.getValue().intValue());

                index.get(key).add(pageEntry);
            }
        }
    }

    @Override
    public Set<PageEntry> search(String word) {
        List<String> list = Arrays.stream(word.split("\\P{IsAlphabetic}+"))
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        list.removeAll(StopList.getStopList());

        if (list.isEmpty()) {
            return Collections.emptySet();
        } else if (list.size() == 1) {
            return index.getOrDefault(list.get(0), Collections.emptySet());
        }

        Set<PageEntry> set = new HashSet<>();
        for (String str : list) {
            set.addAll(index.getOrDefault(str, Collections.emptySet()));
        }

        Map<String, Map<Integer, Integer>> merge = new HashMap<>();
        for (PageEntry pageEntry : set) {
            if (!merge.containsKey(pageEntry.getPdfName())) {
                merge.put(pageEntry.getPdfName(), new HashMap<>());
            }

            Map<Integer, Integer> pages = merge.get(pageEntry.getPdfName());
            pages.merge(pageEntry.getPage(), pageEntry.getCount(), Integer::sum);
        }

        Set<PageEntry> result = new TreeSet<>();
        for (Map.Entry<String, Map<Integer, Integer>> entry : merge.entrySet()) {
            String pdf = entry.getKey();

            for (Map.Entry<Integer, Integer> data : entry.getValue().entrySet()) {
                Integer page = data.getKey();
                Integer count = data.getValue();

                result.add(new PageEntry(pdf, page, count));
            }
        }
        return result;
    }
}