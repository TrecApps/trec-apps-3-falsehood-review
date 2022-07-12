package com.trecapps.falsehoods.falsehoodReview.repos;

import com.trecapps.base.FalsehoodModel.models.PublicFalsehood;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

@Repository
public interface PublicFalsehoodRepo extends JpaRepository<PublicFalsehood, BigInteger> {

}
