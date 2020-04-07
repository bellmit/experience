package etsdb.services;

import etsdb.domains.DataPoint;

public interface RatioReviseService {
    DataPoint revise(DataPoint dp);
}
