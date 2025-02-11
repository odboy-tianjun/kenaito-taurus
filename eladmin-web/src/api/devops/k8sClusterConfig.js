import request from '@/utils/request'

export function searchClusterConfigs(data) {
  return request({
    url: 'api/k8s/clusterConfig/searchClusterConfigs',
    method: 'post',
    data
  })
}

export function listClusterCodes() {
  return request({
    url: 'api/k8s/clusterConfig/listClusterCodes',
    method: 'post'
  })
}

export function createClusterConfig(data) {
  return request({
    url: 'api/k8s/clusterConfig/createClusterConfig',
    method: 'post',
    data
  })
}

export function deleteClusterConfigs(data) {
  return request({
    url: 'api/k8s/clusterConfig/deleteClusterConfigs',
    method: 'post',
    data
  })
}

export default {
  searchClusterConfigs,
  listClusterCodes,
  createClusterConfig,
  deleteClusterConfigs
}
