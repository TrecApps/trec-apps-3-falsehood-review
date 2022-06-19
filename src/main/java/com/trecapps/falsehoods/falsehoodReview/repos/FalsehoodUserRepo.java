package com.trecapps.falsehoods.falsehoodReview.repos;

import com.trecapps.falsehoods.falsehoodReview.models.FalsehoodUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FalsehoodUserRepo extends JpaRepository<FalsehoodUser, String> {
}
