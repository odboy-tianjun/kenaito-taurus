import request from '@/utils/request'

export function searchPwdInfos(data) {
  return request({
    url: 'api/vital/pwdInfo/searchPwdInfos',
    method: 'post',
    data
  })
}

export function createPwdInfo(data) {
  return request({
    url: 'api/vital/pwdInfo/createPwdInfo',
    method: 'post',
    data
  })
}

export function deletePwdInfos(data) {
  return request({
    url: 'api/vital/pwdInfo/deletePwdInfos',
    method: 'post',
    data
  })
}

export function generatePassword(data) {
  return request({
    url: 'api/vital/pwdInfo/generatePassword',
    method: 'post',
    data
  })
}

export default {
  searchPwdInfos,
  createPwdInfo,
  deletePwdInfos,
  generatePassword
}
