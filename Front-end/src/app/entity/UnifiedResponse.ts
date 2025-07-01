
export interface UnifiedResponse {
  data: { [key: string]: { [key: string]: string } };
  timeStamp: string; 
  statusInternalCode: string;
  haveData: boolean;
  haveError: boolean;
}

/*

export interface User {
  userId: string;
  fullName: string;
  email: string;
  totalBalance: string;
  primaryWalletId: string;
  lastUpdated: string;
  walletsCount:string;
  recentTransactionsCount:string;
  wallets:UserWallet[];
  recentTransactions:Transaction[];
  Notifications:Notification[];
  noneReadNotificationsCounter:String;

}

export interface SignupData {
  userName: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  terms: boolean;
}

export interface User{}



export interface Transaction {
  id: string;
  amount: number;
  name: string;
  date: string;
  type: 'shopping' | 'payment' | 'food';
}

export interface UserWallet{
   id:string;
   balance:number;
   currency:string;
   name:string;
   status:string;
}



export interface Notification{
  id:string;
  title:string;
  message:string;
  read:boolean;
  createdDate:Date;
  notificationType:string;

}
*/

export interface SignupData {
  userName: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  terms: boolean;
}

// Add these to your entity/UnifiedResponse.ts file

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  error?: string;
  timestamp?: string;
}

export interface Transaction {
  id: string;
  walletId: string;
  type: 'sent' | 'received' | 'deposit' | 'withdrawal' | 'transfer' | 'payment' | 'refund';
  amount: number;
  currency: string;
  status: 'pending' | 'completed' | 'failed' | 'cancelled' | 'refunded';
  description?: string;
  recipientId?: string;
  recipientEmail?: string;
  recipientPhone?: string;
  senderId?: string;
  category?: string;
  tags?: string[];
  metadata?: Record<string, any>;
  createdAt: Date;
  updatedAt: Date;
  completedAt?: Date;
  failureReason?: string;
}

export interface Notification {
  id: string;
  userId: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  read: boolean;
  actionUrl?: string;
  metadata?: Record<string, any>;
  createdAt: Date;
  updatedAt: Date;
  expiresAt?: Date;
}

export interface UserWallet {
  id: string;
  userId: string;
  name: string;
  type: 'checking' | 'savings' | 'credit' | 'crypto' | 'business' | 'investment';
  balance: number;
  currency: string;
  status: 'active' | 'inactive' | 'suspended' | 'closed';
  description?: string;
  createdAt: Date;
  updatedAt: Date;
  lastTransactionAt?: Date;
}

export interface User {
  id: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  phone?: string;
  avatar?: string;
  status: 'active' | 'inactive' | 'suspended';
  emailVerified: boolean;
  phoneVerified: boolean;
  twoFactorEnabled: boolean;
  primaryWalletId?: string;
  totalBalance: number;
  walletsCount: number;
  recentTransactionsCount: number;
  wallets: UserWallet[];
  recentTransactions: Transaction[];
  Notifications: Notification[];
  createdAt: Date;
  updatedAt: Date;
  lastLoginAt?: Date;
}



// Interface for API responses
export interface VerificationResponse {
  success: boolean;
  message: string;
  data?: any;
}

export interface ResendCodeResponse {
  success: boolean;
  message: string;
  cooldownTime?: number;
}
