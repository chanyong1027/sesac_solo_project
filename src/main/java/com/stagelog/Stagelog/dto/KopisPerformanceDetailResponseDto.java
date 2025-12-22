package com.stagelog.Stagelog.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "dbs")
public class KopisPerformanceDetailResponseDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "db")
    private List<RealKopisPerformanceDetailResponseDto> details;

    public RealKopisPerformanceDetailResponseDto getFirstDetail() {
        return (details != null && !details.isEmpty()) ? details.get(0) : null;
    }
}
