package com.kamal.urlshortener.service.Impl;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.entity.UrlEntity;
import com.kamal.urlshortener.repository.UrlRepository;
import com.kamal.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UrlDto> getUrls() {
        List<UrlEntity> urls = this.urlRepository.findAll();
        return urls.stream()
               .map(urlEntity -> modelMapper.map(urlEntity, UrlDto.class))
               .toList();
    }

    @Override
    public UrlDto getUrlById(Long id) {
       UrlEntity urlEntity = this.urlRepository.findById(id)
               .orElseThrow(() -> new IllegalArgumentException("Url doesn't exist by id: " + id.toString()));
       return modelMapper.map(urlEntity, UrlDto.class);
    }

    @Override
    public UrlDto addUrl(NewUrlDto newUrlDto) {
        UrlEntity newUrlEntity = modelMapper.map(newUrlDto, UrlEntity.class);
        UrlEntity urlEntity = this.urlRepository.save(newUrlEntity);
        return modelMapper.map(urlEntity, UrlDto.class);
    }

    @Override
    public void deleteUrlById(Long id) {
        if(!this.urlRepository.existsById(id)) {
            throw new IllegalArgumentException("Url doesn't exist by id: " + id);
        }
        this.urlRepository.deleteById(id);
    }

    @Override
    public UrlDto updateUrl(Long id, NewUrlDto newUrlDto) {
        UrlEntity urlEntity = this.urlRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Url doesn't exist by id: " + id));
        modelMapper.map(newUrlDto, urlEntity);
        urlEntity = this.urlRepository.save(urlEntity);
        return modelMapper.map(urlEntity, UrlDto.class);
    }

    @Override
    public UrlDto updatePartialUrl(Long id, Map<String, Object> updates) {
        UrlEntity urlEntity = this.urlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Url doesn't exist by id: " + id));

        updates.forEach((field, value) -> {
            switch (field) {
                case "originalUrl":
                    urlEntity.setOriginalUrl((String) value);
                    break;
                case "shortCode":
                    urlEntity.setShortCode((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Field is not supported");
            }
        });

        UrlEntity savedUrlEntity= this.urlRepository.save(urlEntity);
        return modelMapper.map(savedUrlEntity, UrlDto.class);
    }

}
