package net.developerpass.polimi.utils.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponsePage<T> {
	private List<T> content;
	private int number;
	private int size;
	private Long totalElements;

	public PageImpl<T> getPage() {
		return new PageImpl<>(content, PageRequest.of(number, size), totalElements);
	}

}