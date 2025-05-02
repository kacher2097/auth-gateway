package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.dataextractor.DataExtractorRequest;
import com.authenhub.bean.tool.dataextractor.DataExtractorResponse;

public interface IDataExtractorService {
    /**
     * Extract data from a source
     * @param request The data extractor request
     * @return The data extractor response containing the extracted data
     */
    DataExtractorResponse extractData(DataExtractorRequest request);
    
    /**
     * Export extracted data to a file
     * @param format The export format (csv, json, excel)
     * @param dataExtractorResponse The data extractor response to export
     * @return The file content as byte array
     */
    byte[] exportExtractedData(String format, DataExtractorResponse dataExtractorResponse);
}
