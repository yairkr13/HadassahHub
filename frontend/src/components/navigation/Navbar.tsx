import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Logo } from '@/components/common';
import { Button } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/router/routes';

export const Navbar: React.FC = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setIsMobileMenuOpen(false);
    navigate(ROUTES.HOME);
  };

  const closeMobileMenu = () => {
    setIsMobileMenuOpen(false);
  };

  const isActivePath = (path: string) => {
    return location.pathname === path;
  };

  return (
    <nav className="bg-white shadow-soft border-b border-gray-100 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to={ROUTES.HOME} onClick={closeMobileMenu}>
            <Logo />
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            <Link
              to={ROUTES.COURSES}
              className={`transition-colors ${isActivePath(ROUTES.COURSES)
                  ? 'text-primary font-medium'
                  : 'text-text-secondary hover:text-text-primary'
                }`}
            >
              Browse Courses
            </Link>
            {!isAuthenticated && (
              <a
                href="#about"
                className="text-text-secondary hover:text-text-primary transition-colors"
              >
                About
              </a>
            )}
          </div>

          {/* Desktop Right Navigation */}
          <div className="hidden md:flex items-center space-x-4">
            {!isAuthenticated ? (
              // Guest Navigation
              <>
                <Link to={ROUTES.LOGIN}>
                  <Button variant="secondary" size="sm">
                    Sign In
                  </Button>
                </Link>
                <Link to={ROUTES.REGISTER}>
                  <Button variant="primary" size="sm">
                    Register
                  </Button>
                </Link>
              </>
            ) : (
              // Authenticated Navigation
              <>
                <Link
                  to={ROUTES.COURSES}
                  className={`transition-colors ${isActivePath(ROUTES.COURSES)
                      ? 'text-primary font-medium'
                      : 'text-text-secondary hover:text-text-primary'
                    }`}
                >
                  Courses
                </Link>
                <Link
                  to={ROUTES.MY_RESOURCES}
                  className={`transition-colors ${isActivePath(ROUTES.MY_RESOURCES)
                      ? 'text-primary font-medium'
                      : 'text-text-secondary hover:text-text-primary'
                    }`}
                >
                  My Resources
                </Link>
                {user?.role === 'ADMIN' && (
                  <Link
                    to={ROUTES.ADMIN_MODERATION}
                    className={`transition-colors ${isActivePath(ROUTES.ADMIN_MODERATION)
                        ? 'text-primary font-medium'
                        : 'text-text-secondary hover:text-text-primary'
                      }`}
                  >
                    Moderation
                  </Link>
                )}
                <div className="flex items-center space-x-2">
                  <span className="text-sm text-text-secondary">
                    {user?.displayName}
                  </span>
                  <Button variant="secondary" size="sm" onClick={handleLogout}>
                    Logout
                  </Button>
                </div>
              </>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="text-text-secondary hover:text-text-primary focus:outline-none focus:text-text-primary transition-colors"
              aria-label="Toggle mobile menu"
            >
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                {isMobileMenuOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Navigation Menu */}
        {isMobileMenuOpen && (
          <div className="md:hidden border-t border-gray-100 bg-white">
            <div className="px-2 pt-2 pb-3 space-y-1">
              {/* Mobile Navigation Links */}
              <Link
                to={ROUTES.COURSES}
                onClick={closeMobileMenu}
                className={`block px-3 py-2 rounded-md text-base font-medium transition-colors ${isActivePath(ROUTES.COURSES)
                    ? 'text-primary bg-primary/5'
                    : 'text-text-secondary hover:text-text-primary hover:bg-gray-50'
                  }`}
              >
                Browse Courses
              </Link>

              {!isAuthenticated ? (
                // Guest Mobile Navigation
                <>
                  {location.pathname === ROUTES.HOME && (
                    <a
                      href="#about"
                      onClick={closeMobileMenu}
                      className="block px-3 py-2 rounded-md text-base font-medium text-text-secondary hover:text-text-primary hover:bg-gray-50 transition-colors"
                    >
                      About
                    </a>
                  )}
                  <div className="pt-4 pb-2 border-t border-gray-100 space-y-2">
                    <Link to={ROUTES.LOGIN} onClick={closeMobileMenu}>
                      <Button variant="secondary" size="sm" className="w-full">
                        Sign In
                      </Button>
                    </Link>
                    <Link to={ROUTES.REGISTER} onClick={closeMobileMenu}>
                      <Button variant="primary" size="sm" className="w-full">
                        Register
                      </Button>
                    </Link>
                  </div>
                </>
              ) : (
                // Authenticated Mobile Navigation
                <>
                  <Link
                    to={ROUTES.MY_RESOURCES}
                    onClick={closeMobileMenu}
                    className={`block px-3 py-2 rounded-md text-base font-medium transition-colors ${isActivePath(ROUTES.MY_RESOURCES)
                        ? 'text-primary bg-primary/5'
                        : 'text-text-secondary hover:text-text-primary hover:bg-gray-50'
                      }`}
                  >
                    My Resources
                  </Link>

                  {user?.role === 'ADMIN' && (
                    <Link
                      to={ROUTES.ADMIN_MODERATION}
                      onClick={closeMobileMenu}
                      className={`block px-3 py-2 rounded-md text-base font-medium transition-colors ${isActivePath(ROUTES.ADMIN_MODERATION)
                          ? 'text-primary bg-primary/5'
                          : 'text-text-secondary hover:text-text-primary hover:bg-gray-50'
                        }`}
                    >
                      Moderation
                    </Link>
                  )}

                  <div className="pt-4 pb-2 border-t border-gray-100">
                    <div className="px-3 py-2">
                      <p className="text-sm text-text-secondary">
                        Signed in as <span className="font-medium text-text-primary">{user?.displayName}</span>
                      </p>
                    </div>
                    <Button
                      variant="secondary"
                      size="sm"
                      className="w-full mx-3"
                      onClick={handleLogout}
                    >
                      Logout
                    </Button>
                  </div>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};