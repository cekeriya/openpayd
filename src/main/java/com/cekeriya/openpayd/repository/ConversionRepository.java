package com.cekeriya.openpayd.repository;

import com.cekeriya.openpayd.model.Conversion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, UUID> {
	Optional<Conversion> findByTransactionId(UUID transactionId);

	List<Conversion> findByConversionDateLessThan(Date conversionDate, Pageable pageable);

}
