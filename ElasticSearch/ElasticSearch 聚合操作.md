这个思否上的

https://segmentfault.com/a/1190000014946753

# Count

## 官方的

https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-valuecount-aggregation.html

```json
{
    "aggs": {
        "types_count": {
            "value_count": {
                "field": "DestWeather"
            }
        }
    }
}
```

返回结果

```json
{
  "took" : 39,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "p-v_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "LVK6HFM",
          "DestCountry" : "AR",
          "OriginWeather" : "Clear",
          "OriginCityName" : "Frankfurt am Main",
          "AvgTicketPrice" : 1199.6428164485324,
          "DistanceMiles" : 7132.8067215172905,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Ministro Pistarini International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "DE",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 11479.139700433523,
          "timestamp" : "2020-08-08T01:25:26",
          "DestLocation" : {
            "lat" : "-34.8222",
            "lon" : "-58.5358"
          },
          "DestAirportID" : "EZE",
          "Carrier" : "JetBeats",
          "Cancelled" : false,
          "FlightTimeMin" : 1043.5581545848656,
          "Origin" : "Frankfurt am Main Airport",
          "OriginLocation" : {
            "lat" : "50.033333",
            "lon" : "8.570556"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "FRA",
          "OriginRegion" : "DE-HE",
          "DestCityName" : "Buenos Aires",
          "FlightTimeHour" : 17.39263590974776,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.6428,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.5123,
          0
        ]
      }
    ]
  },
  "aggregations" : {
    "types_count" : {
      "value" : 13059
    }
  }
}
```

## 简书上的

没有，那个上面没有单个 Count 操作，只有 Distinct + Count

# Group by

## 官网的

https://www.elastic.co/guide/cn/elasticsearch/guide/current/_preventing_combinatorial_explosions.html

```json
{
    "aggs": {
        "groupBys": {
            "terms": {
                "field": "DestWeather"
            },
            "aggs": {
                "costars": {
                    "terms": {
                        "field": "DestWeather"
                    }
                }
            }
        }
    }
}
```

返回结果

```json
{
  "took" : 15,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "p-v_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "LVK6HFM",
          "DestCountry" : "AR",
          "OriginWeather" : "Clear",
          "OriginCityName" : "Frankfurt am Main",
          "AvgTicketPrice" : 1199.6428164485324,
          "DistanceMiles" : 7132.8067215172905,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Ministro Pistarini International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "DE",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 11479.139700433523,
          "timestamp" : "2020-08-08T01:25:26",
          "DestLocation" : {
            "lat" : "-34.8222",
            "lon" : "-58.5358"
          },
          "DestAirportID" : "EZE",
          "Carrier" : "JetBeats",
          "Cancelled" : false,
          "FlightTimeMin" : 1043.5581545848656,
          "Origin" : "Frankfurt am Main Airport",
          "OriginLocation" : {
            "lat" : "50.033333",
            "lon" : "8.570556"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "FRA",
          "OriginRegion" : "DE-HE",
          "DestCityName" : "Buenos Aires",
          "FlightTimeHour" : 17.39263590974776,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.6428,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.5123,
          0
        ]
      }
    ]
  },
  "aggregations" : {
    "groupBys" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "Rain",
          "doc_count" : 2359,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Rain",
                "doc_count" : 2359
              }
            ]
          }
        },
        {
          "key" : "Clear",
          "doc_count" : 2336,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Clear",
                "doc_count" : 2336
              }
            ]
          }
        },
        {
          "key" : "Sunny",
          "doc_count" : 2275,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Sunny",
                "doc_count" : 2275
              }
            ]
          }
        },
        {
          "key" : "Cloudy",
          "doc_count" : 2221,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Cloudy",
                "doc_count" : 2221
              }
            ]
          }
        },
        {
          "key" : "Heavy Fog",
          "doc_count" : 989,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Heavy Fog",
                "doc_count" : 989
              }
            ]
          }
        },
        {
          "key" : "Thunder & Lightning",
          "doc_count" : 979,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Thunder & Lightning",
                "doc_count" : 979
              }
            ]
          }
        },
        {
          "key" : "Damaging Wind",
          "doc_count" : 952,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Damaging Wind",
                "doc_count" : 952
              }
            ]
          }
        },
        {
          "key" : "Hail",
          "doc_count" : 948,
          "costars" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "Hail",
                "doc_count" : 948
              }
            ]
          }
        }
      ]
    }
  }
}
```

# Distinct

按照目的地天气去重

## 搜到的

搜到的应该是不正确的

https://discuss.elastic.co/t/how-to-return-distinct-values-from-query-based-on-a-field/228000/2

搜索第五个和第七个 terms

```json
{
    "agg": {
        "distincts": {
            "terms": {
                "field": "DestWeather"
            }
        }
    }
}
```
返回值

```json
{
  "took" : 42,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "p-v_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "LVK6HFM",
          "DestCountry" : "AR",
          "OriginWeather" : "Clear",
          "OriginCityName" : "Frankfurt am Main",
          "AvgTicketPrice" : 1199.6428164485324,
          "DistanceMiles" : 7132.8067215172905,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Ministro Pistarini International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "DE",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 11479.139700433523,
          "timestamp" : "2020-08-08T01:25:26",
          "DestLocation" : {
            "lat" : "-34.8222",
            "lon" : "-58.5358"
          },
          "DestAirportID" : "EZE",
          "Carrier" : "JetBeats",
          "Cancelled" : false,
          "FlightTimeMin" : 1043.5581545848656,
          "Origin" : "Frankfurt am Main Airport",
          "OriginLocation" : {
            "lat" : "50.033333",
            "lon" : "8.570556"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "FRA",
          "OriginRegion" : "DE-HE",
          "DestCityName" : "Buenos Aires",
          "FlightTimeHour" : 17.39263590974776,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.6428,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.5123,
          0
        ]
      }
    ]
  },
  "aggregations" : {
    "distincts" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "Rain",
          "doc_count" : 2359
        },
        {
          "key" : "Clear",
          "doc_count" : 2336
        },
        {
          "key" : "Sunny",
          "doc_count" : 2275
        },
        {
          "key" : "Cloudy",
          "doc_count" : 2221
        },
        {
          "key" : "Heavy Fog",
          "doc_count" : 989
        },
        {
          "key" : "Thunder & Lightning",
          "doc_count" : 979
        },
        {
          "key" : "Damaging Wind",
          "doc_count" : 952
        },
        {
          "key" : "Hail",
          "doc_count" : 948
        }
      ]
    }
  }
}
```





## 简书上的

```json
{
    "collapse": {
        "field": "user_id"
    }
}
```

这两个返回值首先不一样

```json
{
  "took" : 17,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "fields" : {
          "DestWeather" : [
            "Cloudy"
          ]
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "fields" : {
          "DestWeather" : [
            "Clear"
          ]
        },
        "sort" : [
          1199.5123,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "BOv_c3MByXoeBBO0q0ML",
        "_score" : null,
        "_source" : {
          "FlightNum" : "8322YWS",
          "DestCountry" : "US",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Brisbane",
          "AvgTicketPrice" : 1199.403468490791,
          "DistanceMiles" : 9502.211070996447,
          "FlightDelay" : false,
          "DestWeather" : "Rain",
          "Dest" : "Syracuse Hancock International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "AU",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 15292.326373841706,
          "timestamp" : "2020-08-08T13:47:47",
          "DestLocation" : {
            "lat" : "43.11119843",
            "lon" : "-76.10630035"
          },
          "DestAirportID" : "SYR",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 764.6163186920853,
          "Origin" : "Brisbane International Airport",
          "OriginLocation" : {
            "lat" : "-27.38419914",
            "lon" : "153.1170044"
          },
          "DestRegion" : "US-NY",
          "OriginAirportID" : "BNE",
          "OriginRegion" : "SE-BD",
          "DestCityName" : "Syracuse",
          "FlightTimeHour" : 12.743605311534756,
          "FlightDelayMin" : 0
        },
        "fields" : {
          "DestWeather" : [
            "Rain"
          ]
        },
        "sort" : [
          1199.4034,
          0
        ]
      }
    ]
  }
}
```

# Distinct + Count

## 搜到的

https://stackoverflow.com/questions/42885532/count-distinct-on-elastic-search/44501242

好像还是不对的，这个应该是 group by 后的结果。

```json
{
    "aggs": {
        "count": {
            "terms": {
                "field": "DestWeather"
            },
            "aggs": {
                "unique_invoiceid": {
                    "cardinality": {
                        "field": "DestWeather"
                    }
                }
            }
        }
    }
}
```

返回结果

```json
{
  "took" : 51,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "p-v_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "LVK6HFM",
          "DestCountry" : "AR",
          "OriginWeather" : "Clear",
          "OriginCityName" : "Frankfurt am Main",
          "AvgTicketPrice" : 1199.6428164485324,
          "DistanceMiles" : 7132.8067215172905,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Ministro Pistarini International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "DE",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 11479.139700433523,
          "timestamp" : "2020-08-08T01:25:26",
          "DestLocation" : {
            "lat" : "-34.8222",
            "lon" : "-58.5358"
          },
          "DestAirportID" : "EZE",
          "Carrier" : "JetBeats",
          "Cancelled" : false,
          "FlightTimeMin" : 1043.5581545848656,
          "Origin" : "Frankfurt am Main Airport",
          "OriginLocation" : {
            "lat" : "50.033333",
            "lon" : "8.570556"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "FRA",
          "OriginRegion" : "DE-HE",
          "DestCityName" : "Buenos Aires",
          "FlightTimeHour" : 17.39263590974776,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.6428,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.5123,
          0
        ]
      }
    ]
  },
  "aggregations" : {
    "count" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "Rain",
          "doc_count" : 2359,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Clear",
          "doc_count" : 2336,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Sunny",
          "doc_count" : 2275,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Cloudy",
          "doc_count" : 2221,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Heavy Fog",
          "doc_count" : 989,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Thunder & Lightning",
          "doc_count" : 979,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Damaging Wind",
          "doc_count" : 952,
          "unique_invoiceid" : {
            "value" : 1
          }
        },
        {
          "key" : "Hail",
          "doc_count" : 948,
          "unique_invoiceid" : {
            "value" : 1
          }
        }
      ]
    }
  }
}
```

## 简书上的

```json
{
    "aggs": {
        "count": {
            "cardinality": {
                "field": "DestWeather"
            }
        }
    }
}
```

返回结果

```json
{
  "took" : 60,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "p-v_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "LVK6HFM",
          "DestCountry" : "AR",
          "OriginWeather" : "Clear",
          "OriginCityName" : "Frankfurt am Main",
          "AvgTicketPrice" : 1199.6428164485324,
          "DistanceMiles" : 7132.8067215172905,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Ministro Pistarini International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "DE",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 11479.139700433523,
          "timestamp" : "2020-08-08T01:25:26",
          "DestLocation" : {
            "lat" : "-34.8222",
            "lon" : "-58.5358"
          },
          "DestAirportID" : "EZE",
          "Carrier" : "JetBeats",
          "Cancelled" : false,
          "FlightTimeMin" : 1043.5581545848656,
          "Origin" : "Frankfurt am Main Airport",
          "OriginLocation" : {
            "lat" : "50.033333",
            "lon" : "8.570556"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "FRA",
          "OriginRegion" : "DE-HE",
          "DestCityName" : "Buenos Aires",
          "FlightTimeHour" : 17.39263590974776,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.6428,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.5123,
          0
        ]
      }
    ]
  },
  "aggregations" : {
    "count" : {
      "value" : 8
    }
  }
}
```

# Distinct + Count + Group By

## 简书上的

```json
{
    "aggs": {
        "distincts": {
            "terms": {
                "field": "Carrier"
            },
            "aggs": {
                "count": {
                    "cardinality": {
                        "field": "DestWeather"
                    }
                }
            }
        }
    }
}
```

返回结果

```json
{
  "took" : 20,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "fev_c3MByXoeBBO0hAVk",
        "_score" : null,
        "_source" : {
          "FlightNum" : "C2YBQ05",
          "DestCountry" : "US",
          "OriginWeather" : "Clear",
          "OriginCityName" : "London",
          "AvgTicketPrice" : 1199.7290528077556,
          "DistanceMiles" : 4660.234093788673,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Spokane International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "GB",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7499.919777434238,
          "timestamp" : "2020-07-18T14:35:57",
          "DestLocation" : {
            "lat" : "47.61989975",
            "lon" : "-117.5339966"
          },
          "DestAirportID" : "GEG",
          "Carrier" : "Logstash Airways",
          "Cancelled" : false,
          "FlightTimeMin" : 535.708555531017,
          "Origin" : "London Heathrow Airport",
          "OriginLocation" : {
            "lat" : "51.4706",
            "lon" : "-0.461941"
          },
          "DestRegion" : "US-WA",
          "OriginAirportID" : "LHR",
          "OriginRegion" : "GB-ENG",
          "DestCityName" : "Spokane",
          "FlightTimeHour" : 8.92847592551695,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.729,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "p-v_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "LVK6HFM",
          "DestCountry" : "AR",
          "OriginWeather" : "Clear",
          "OriginCityName" : "Frankfurt am Main",
          "AvgTicketPrice" : 1199.6428164485324,
          "DistanceMiles" : 7132.8067215172905,
          "FlightDelay" : false,
          "DestWeather" : "Cloudy",
          "Dest" : "Ministro Pistarini International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "DE",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 11479.139700433523,
          "timestamp" : "2020-08-08T01:25:26",
          "DestLocation" : {
            "lat" : "-34.8222",
            "lon" : "-58.5358"
          },
          "DestAirportID" : "EZE",
          "Carrier" : "JetBeats",
          "Cancelled" : false,
          "FlightTimeMin" : 1043.5581545848656,
          "Origin" : "Frankfurt am Main Airport",
          "OriginLocation" : {
            "lat" : "50.033333",
            "lon" : "8.570556"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "FRA",
          "OriginRegion" : "DE-HE",
          "DestCityName" : "Buenos Aires",
          "FlightTimeHour" : 17.39263590974776,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.6428,
          0
        ]
      },
      {
        "_index" : "kibana_sample_data_flights",
        "_type" : "_doc",
        "_id" : "MOv_c3MByXoeBBO0q0IL",
        "_score" : null,
        "_source" : {
          "FlightNum" : "CVXI3Y9",
          "DestCountry" : "CL",
          "OriginWeather" : "Cloudy",
          "OriginCityName" : "Birmingham",
          "AvgTicketPrice" : 1199.5123420032087,
          "DistanceMiles" : 4720.930062839279,
          "FlightDelay" : false,
          "DestWeather" : "Clear",
          "Dest" : "Comodoro Arturo Merino Benitez International Airport",
          "FlightDelayType" : "No Delay",
          "OriginCountry" : "US",
          "dayOfWeek" : 5,
          "DistanceKilometers" : 7597.600471050017,
          "timestamp" : "2020-08-08T18:06:23",
          "DestLocation" : {
            "lat" : "-33.39300156",
            "lon" : "-70.78579712"
          },
          "DestAirportID" : "SCL",
          "Carrier" : "ES-Air",
          "Cancelled" : false,
          "FlightTimeMin" : 542.6857479321441,
          "Origin" : "Birmingham-Shuttlesworth International Airport",
          "OriginLocation" : {
            "lat" : "33.56290054",
            "lon" : "-86.75350189"
          },
          "DestRegion" : "SE-BD",
          "OriginAirportID" : "BHM",
          "OriginRegion" : "US-AL",
          "DestCityName" : "Santiago",
          "FlightTimeHour" : 9.044762465535735,
          "FlightDelayMin" : 0
        },
        "sort" : [
          1199.5123,
          0
        ]
      }
    ]
  },
  "aggregations" : {
    "distincts" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "Logstash Airways",
          "doc_count" : 3331,
          "count" : {
            "value" : 8
          }
        },
        {
          "key" : "JetBeats",
          "doc_count" : 3274,
          "count" : {
            "value" : 8
          }
        },
        {
          "key" : "Kibana Airlines",
          "doc_count" : 3234,
          "count" : {
            "value" : 8
          }
        },
        {
          "key" : "ES-Air",
          "doc_count" : 3220,
          "count" : {
            "value" : 8
          }
        }
      ]
    }
  }
}
```

# 问题

这个组合起来是不固定的，一会采用内聚 aggs 的方式，一会用普通的，和单个解析不一样。这个怎么办，返回的解析的内容也是不一样的。

