export interface UnifiedResponse {
  data: { [key: string]: { [key: string]: string } };
  timeStamp: string;
  statusInternalCode: string;
  haveData: boolean;
  haveError: boolean;
}

export class ResponseExtractor {
  
  static getDataKeys(response: UnifiedResponse): string[] {
    return Object.keys(response.data);
  }

  static getInternalKeys(response: UnifiedResponse, dataKey: string): string[] {
    return response.data[dataKey] ? Object.keys(response.data[dataKey]) : [];
  }

  static extractData(response: UnifiedResponse, dataKey: string, internalKey?: string): any {
    const availableDataKeys = this.getDataKeys(response);
    
    if (!dataKey || !response.data[dataKey]) {
      throw new Error(`Data key '${dataKey}' not found. Available keys: ${availableDataKeys.join(', ')}`);
    }

    const availableInternalKeys = this.getInternalKeys(response, dataKey);
    let key = internalKey || availableInternalKeys[0];

    if (!key || !response.data[dataKey][key]) {
      throw new Error(`Internal key '${key}' not found. Available keys: ${availableInternalKeys.join(', ')}`);
    }

    try {
      const rawData = response.data[dataKey][key];
      return JSON.parse(rawData);
    } catch (error) {
      throw new Error(`Parse error: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  }
}

// Usage - just get the raw data and let user parse it themselves:
/*
const rawUserData = ResponseExtractor.extractData(response, 'data', 'userInfo');

// User can now parse it to their own class however they want
const user = new User();
Object.assign(user, rawUserData);
// or use a custom mapper, constructor, etc.
*/