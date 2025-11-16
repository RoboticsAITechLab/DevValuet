package com.devvault.cockpit.core.dataset;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatasetService {

    public DatasetMetadata extractMetadata(Dataset dataset) {
        if (dataset == null || dataset.getPath() == null) return new DatasetMetadata(0, 0, List.of());
        Path p = Path.of(dataset.getPath());
        if (!Files.exists(p)) return new DatasetMetadata(0, 0, List.of());

        try {
            var stream = Files.walk(p)
                    .filter(Files::isRegularFile);
            long totalSize = stream.mapToLong(fp -> {
                try { return Files.size(fp); } catch (IOException e) { return 0L; }
            }).sum();
            // need to walk again for count and samples
            long count = Files.walk(p).filter(Files::isRegularFile).count();
            List<String> samples = Files.walk(p)
                    .filter(Files::isRegularFile)
                    .limit(10)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            return new DatasetMetadata(count, totalSize, samples);
        } catch (IOException e) {
            return new DatasetMetadata(0, 0, List.of());
        }
    }
}
