module "curated-alignments-demo" {
    source            = "./iap_cloudrun"
    demo_name         = "curated-alignments"
    domain            = "curated-alignments-demo.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/curated-alignments-demo:2"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
    memory = "2Gi"
}
module "showcase-demo-for-researchers" {
    source            = "./iap_cloudrun"
    demo_name         = "showcase-demo-for-researchers"
    domain            = "showcase-demo-for-researchers.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/sc-161-showcase-demo-for-researchers:live"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
}

module "search-bubbles-topics-autocomplete" {
    source            = "./iap_cloudrun"
    demo_name         = "search-bubbles-topics-autocomplete"
    domain            = "search-bubbles-topics-autocomplete.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/sc-268-search-bubbles-topics:autocomplete"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
}

module "search-bubbles-topics" {
    source            = "./iap_cloudrun"
    demo_name         = "search-bubbles-topics"
    domain            = "search-bubbles-topics.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/sc-268-search-bubbles-topics:live"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
}

module "sensitive-topic-tagger" {
    source            = "./iap_cloudrun"
    demo_name         = "sensitive-topic-tagger"
    domain            = "sensitive-topic-tagger.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/sensitive_topic_tagger:live"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
}

module "showcase-api-demo" {
    source            = "./iap_cloudrun"
    demo_name         = "showcase-api-demo"
    domain            = "showcase-api-demo.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/showcase-api-demo:live"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
}

module "rel-videos-poc" {
    source            = "./iap_cloudrun"
    demo_name         = "rel-videos-poc"
    domain            = "rel-videos-poc.projectX-labs.com"
    region            = var.region
    image             = "eu.gcr.io/projectX-data-science/sc-270-rel-videos-poc:live"
    iap_client_id     = var.iap_client_id
    iap_client_secret = var.iap_client_secret
    serverless-vpc-connector = var.serverless_vpc_access_connector_name
}
